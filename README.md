# Brewkery ☕

A small native Android app for a coffee and bakery shop, built for the Clickretina Android assignment. You browse the menu, customize an item, add it to a cart, and place an order.

Flow: **Menu → Item Detail → Cart → Order Status**

I tried to match the provided mockups as closely as I reasonably could, and to keep the code simple instead of clever. There's no database, no dependency injection framework and no multi-module setup, because the brief didn't ask for any of that.

## What it does

- **Menu:** loads items from the API, with category chips, search, ratings, badges and a store/active-order banner.
- **Item detail:** hero image, ingredients and customizations (size, milk, sugar). The price updates live as you pick options.
- **Cart:** quantity stepper, subtotal, $2.50 delivery fee and 8% tax. Identical items with identical options merge into one line. Clear cart is supported.
- **Order status:** placing an order clears the cart, creates a ticket like `#BK-74921` and shows `PREPARING`. The menu then shows an "Active Order" banner with a Track button.
- **Loading and error states:** a spinner while loading, and a message with a Retry button if the network fails.
- **Toasts:** small top notifications for add to cart, cart cleared and order placed, like the mockups.

## Tech stack

| Area | Choice |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Networking | Retrofit + Gson + Coroutines |
| Images | Coil |
| Navigation | Navigation Compose |
| State | One shared `ViewModel` with `StateFlow` |
| Tests | JUnit |

Min SDK 24.

## Project structure

```
app/src/main/java/com/example/brewkery/
├── MainActivity.kt          # navigation graph + toast host
├── data/
│   ├── Models.kt            # API data classes
│   ├── BrewkeryApi.kt       # Retrofit interface + client
│   └── PriceCalculator.kt   # cart line + price/tax maths (pure Kotlin)
└── ui/
    ├── BrewViewModel.kt     # menu state, cart, order, toast
    ├── Theme.kt, Components.kt
    └── HomeScreen / DetailScreen / CartScreen / OrderStatusScreen.kt
```

## A few decisions worth explaining

- **One API call.** `data.json` already contains the store settings, categories, and every item with its customizations, so I didn't call `api/items/{id}.json` for the detail screen. That keeps the detail screen instant and removes a second loading and error state.
- **Price maths lives in a plain Kotlin object** (`PriceCalculator`) instead of inside the UI or ViewModel. That's what makes it easy to unit test. Tax is calculated on the subtotal only, and everything is rounded to cents.
- **An empty cart totals $0.00** even though the delivery fee row still shows $2.50, which is how the mockup behaves.
- **The cart lives in memory.** It resets when the app is killed. The brief said no database was needed, so I left it out.
- **One ViewModel for the whole app.** It's small enough that splitting it per screen would just be extra ceremony.

## Running it

1. Open the project in a recent Android Studio and let Gradle sync.
2. Run it on an emulator or a phone. It needs internet for the menu and images.

Debug APK: `app/build/outputs/apk/debug/app-debug.apk` (or run `./gradlew assembleDebug`).

## Tests

```
./gradlew testDebugUnitTest
```

`PriceCalculatorTest` covers the unit price with size and milk extras, the two worked examples from the mockups ($9.40 → $12.65 and $4.85 → $7.74), and the empty cart.

## How I used AI

This was an AI-first assignment, so here's what that actually looked like.

**Tool:** Claude (Anthropic), in the chat app. I wrote and ran everything in Android Studio myself, then went back to Claude when something broke or looked off.

**Prompts I sent (real ones):**

1. My opening prompt (sent with the UI screenshots for every screen and state, plus the assignment link):

   > I want you to help me build this native Android application using Kotlin.
   >
   > I have uploaded the UI screenshots for all the screens and different states/actions, and I have also provided the instruction/reference link. First, carefully read and understand the complete instructions and study all the uploaded screenshots. Understand the complete user flow, UI, required functionality, API structure, calculations, and technical requirements before starting the implementation.
   >
   > Use the technologies and libraries specified in the instructions. Follow the provided API and requirements exactly.
   >
   > The UI should closely match the provided screenshots and reference implementation. Treat the screenshots as the primary UI reference. Reproduce the layout, spacing, typography, colors, cards, buttons, icons, images, controls, navigation, and different UI states as accurately as reasonably possible. Do not redesign the UI or introduce your own design unless something is not specified.
   >
   > Keep the implementation straightforward and practical. Do not over-engineer the application. Use a clean and sensible architecture, but avoid unnecessary abstraction layers, interfaces, repositories, modules, libraries, or patterns that are not useful for this project. Implement only what is required by the instructions and screenshots.
   >
   > I want you to write the actual source code for the application, not just explain how it could be implemented.
   >
   > Start by defining the complete project/file structure. Then provide the code file-by-file.
   >
   > For every file:
   > - Give the exact file path.
   > - Provide the complete code.
   > - Include all required imports.
   > - Make sure the code is consistent with the other files.
   > - Do not leave placeholder implementations or unfinished functions.
   > - Do not omit supporting classes/files that are required for the code to compile.
   > - Clearly mention when an existing file needs to be modified rather than creating a duplicate file.
   >
   > Also provide all required project configuration, including:
   > - Gradle configuration
   > - Dependencies and compatible versions
   > - AndroidManifest configuration
   > - Permissions
   > - Compose configuration
   > - API/network configuration
   > - Any other required setup
   >
   > Use the versions and technologies that are appropriate for the project and make sure the dependencies are compatible with each other.
   >
   > The implementation should include everything explicitly required by the instructions, including API integration, models, networking, UI states, navigation, product details, customization, dynamic pricing, cart functionality, calculations, order placement, order status, loading/error handling, and the required unit test.
   >
   > Use the actual API provided in the instructions. Do not replace API-driven data with hardcoded data.
   >
   > For the UI, use the uploaded screenshots throughout the implementation. If a screen has multiple screenshots showing different interactions or states, implement those states and transitions accordingly.
   >
   > Keep the code production-quality but appropriately scoped for this application. Prioritize correctness, visual accuracy, readability, and completing the required functionality over adding unnecessary complexity.
   >
   > Do not build a different application based on your own assumptions. The provided instructions, screenshots, and reference implementation should be treated as the source of truth.
   >
   > Do not generate the entire project in one unnecessarily huge response. Organize the implementation into logical sections/files so that I can copy the files directly into Android Studio. However, whenever you provide a file, provide its complete contents so I can use it directly.
   >
   > Before writing the implementation, briefly summarize the architecture and file structure you are going to use. Then start with the project configuration and source files.

2. *"you missed to add these type of alert notification. add it"* (with screenshots of the three toast messages)

3. *"These boxes are not equal. Adjust them"* (with a screenshot from my phone of the uneven size boxes)

**What AI got right:**
It read the real API and built the data models from the actual JSON, instead of guessing field names. It also worked out the price and tax rules from the mockup numbers (tax on the subtotal only, empty cart = $0.00) and turned them into unit tests, so I could check the maths against the design instead of trusting it.

**What AI got wrong, and how I fixed it:**
Two things.
- The first version failed to compile with a pile of `Unresolved reference 'icons'` errors. The code used Compose icons, but the icon library wasn't in my project, because newer Compose versions don't include it automatically. I pasted the error screenshot back, and the fix was one extra dependency, `material-icons-core`.
- It also built the first version without the toast notifications, because it could only read the text of the design page at first and had no screenshots. After I uploaded the real screens it added them. Later, the size options had uneven heights on my phone, and that was fixed with equal-height boxes and tighter padding.

**What I took from it:** AI was fast at the boilerplate (models, navigation, layouts), but it can't see your screen or your build. Running the app, reading the errors and comparing against the mockup were still my job.

## What I'd do next

- Persist the cart and the last order (Room or DataStore).
- Move the cart into its own repository and split the ViewModel as the app grows.
- Add UI tests for the add-to-cart flow.
- Use the design's custom font instead of the system font.
