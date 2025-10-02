# BTO application made using Java and OOP principles.
 
## How to Run & Contribute

### Running the App

The **main entry point** for this CLI app is the `main()` function in:

```
main/MainMenu.java
```

This file also contains the **main render loop** that updates the terminal UI and listens for user input.  
To run the application, simply execute `MainMenu.java` — no other file contains a `main()` function.

---

### UI Architecture (ui/)

The `ui/` package contains a **custom terminal UI framework** designed for rendering boxes, lists, fields, and other elements in a modular and styled way.

#### This package includes:
- **UI components** – `BoxObject`, `ListObject`, `Field`, etc.
- **UI data types** – e.g., `DisplayObject` which represents UI items
- **Helper logic** – for layout, rendering, and string formatting
- **`UserInterface` class** – an encapsulating interface to help your code plug into the UI easily


> ⚠️ **Do not modify existing code inside `ui/`.**  
> These components are tightly integrated. If you want to add a new UI feature or component, **DM me directly** and I'll add it in safely.

---

### Working with Data: `models/`

The `models/` folder is where you should define **custom data types** that power your menu features.

For example:
- If you're working with projects, create a `Project.java` object
- If you're managing inquiries, create an `Enquiry.java` object

---

### Application Logic: `app/`

Store all **manager classes** (business/BTO logic) inside the `app/` folder.  
Each manager handles logic for a specific menu in the app, such as:

- `SampleProjectManager` – Handles project creation, filtering, loading/saving, etc.
- `SampleEnquiryManager` – Handles inquiries submitted via the menu

Feel free to create your own manager classes for new menu sections. Keep each manager focused and testable.

---

### Project Structure

```
src/
│
├── ui/                         <-- Custom CLI UI framework
│   ├── UserInterface.java
│   ├── DisplayObject.java
│   ├── Field.java
│   ├── BoxObject.java
│   ├── ListObject.java
│   └── Terminal.java
│
├── app/                        <-- Application logic (menu-specific logic)
│   ├── SampleProjectManager.java
│   └── SampleEnquiryManager.java
│
├── main/                       <-- Main loop
│   └── MainMenu.java
│
└── model/                      <-- Custom data types
    ├── Project.java
    └── Enquiry.java
```

---
