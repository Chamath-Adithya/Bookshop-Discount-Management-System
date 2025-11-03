# 📚 Project Learning & Presentation Guide

## 1. Introduction

This guide is designed to help each team member understand the project on a deeper level, beyond just completing coding tasks. While working together is great for building the project, your presentation will require each of you to explain your work and its connection to core software engineering principles in detail.

Use this guide to study your part of the project and prepare to present it like an expert.

---

## 2. Connecting Your Code to OOP Concepts

This project is a practical demonstration of Object-Oriented Programming. Here’s how to identify and explain the four core OOP principles in your work.

### 🔹 Encapsulation (The Protector)

-   **What it is:** Bundling data (fields) and the methods that work on that data within a single class. It means protecting the data by making fields `private` and providing `public` methods (getters/setters) to access it.
-   **Where to find it:** The `Product`, `Customer`, and `User` classes are perfect examples. They have `private` fields that can only be accessed through their public methods.
-   **How to talk about it:**
    -   **Ask yourself:** "Why are the fields in `Product.java` private? What could go wrong if another class could directly change a product's price without using a method?"
    -   **For your presentation:** "In our project, we used encapsulation to protect our data integrity. For example, the `Product` class encapsulates its `realPrice`. By making it private, we ensure that it can only be accessed or modified through controlled methods, preventing accidental or unauthorized changes."

### 🔹 Inheritance (The Re-user)

-   **What it is:** Creating a new "child" class from an existing "parent" class. The child inherits the parent's attributes and methods, allowing you to reuse code and create a logical hierarchy.
-   **Where to find it:**
    1.  `RegularCustomer` and `VIPCustomer` both inherit from the `Customer` class.
    2.  `Manager` and `Worker` both inherit from the `User` class.
-   **How to talk about it:**
    -   **Ask yourself:** "What do `VIPCustomer` and `RegularCustomer` have in common? Why is it better to define these common things once in a `Customer` class instead of rewriting them in both child classes?"
    -   **For your presentation:** "We leveraged inheritance to model our customer types. Both `VIPCustomer` and `RegularCustomer` are types of `Customer`, so they `extend` the base `Customer` class. This allows them to inherit common properties like `customerId` and `name`, promoting code reuse and creating a clear 'is-a' relationship."

### 🔹 Polymorphism (The Shape-shifter)

-   **What it is:** The ability for something to have "many forms." In our code, it means we can treat a `VIPCustomer` object as a `Customer` object, but when we call a method like `calculateFinalPrice()`, the specific `VIPCustomer` version of that method is executed.
-   **Where to find it:** The `calculateFinalPrice(double totalAmount)` method is the key example. It is defined in the `Customer` class but is implemented differently in `RegularCustomer` and `VIPCustomer`.
-   **How to talk about it:**
    -   **Ask yourself:** "How can the `BillingService` calculate a final bill without using an `if` statement to check `if (customer is VIP)`? How does it call the same method on different object types and get different results?"
    -   **For your presentation:** "Our design demonstrates polymorphism through the `calculateFinalPrice` method. The `BillingService` works with a generic `Customer` object. It doesn't know or care if it's a `RegularCustomer` or a `VIPCustomer`. It simply calls the method, and the Java Virtual Machine ensures that the correct, overridden version of the method is executed, automatically applying the VIP discount only when needed."

### 🔹 Abstraction (The Simplifier)

-   **What it is:** Hiding complex implementation details and showing only the essential features. `abstract` classes are a primary way to achieve this. They define a "contract" that child classes must follow.
-   **Where to find it:** The `Customer` and `User` classes are `abstract`. You cannot create a `new Customer()`; you must create an instance of a *concrete* child class like `new VIPCustomer()`.
-   **How to talk about it:**
    -   **Ask yourself:** "Why did we make the `Customer` class `abstract`? What does the `abstract` method inside it force the child classes to do?"
    -   **For your presentation:** "We used abstraction to define a clear template for our models. The `Customer` class is `abstract` because there is no such thing as a 'generic' customer in our system—every customer must be either Regular or VIP. The abstract `calculateFinalPrice` method acts as a contract, forcing any new customer type we might create in the future to provide its own pricing logic, which makes our system extensible and robust."

---

## 3. Presentation Preparation Tips

1.  **Be the Expert on Your Part:** You must be able to explain the code you wrote line-by-line. Why did you choose that data type? What does that method do? What happens if the input is invalid?

2.  **Understand the Connections:** Know how your code interacts with the rest of the project. If you are **Team Member A** working on `FileHandler`, you need to explain how **Team Member C**'s `ProductService` depends on your parsing logic.

3.  **Use the Vocabulary:** When presenting, use the OOP terms above. Don't just say "this class is the parent"; say "we used inheritance to create a parent class, `Customer`...". Point to your code as the evidence.

4.  **Practice Explaining the "Why":** The most important question is not *what* your code does, but *why* it does it that way. Why was inheritance a good choice here? Why was encapsulation important for that class? Answering the "why" demonstrates a true understanding of the design.

---

## 4. Learning Resources and AI Prompts

Use these resources and prompts to deepen your understanding of the core concepts.

### **Encapsulation**

-   **Reference:** [Oracle's Java Tutorials on Encapsulation](https://docs.oracle.com/javase/tutorial/java/javaOO/encapsulation.html)
-   **Learning Activity:** In the `Product.java` class, temporarily change the `realPrice` field from `private` to `public`. Now, from another class (like `BillingService`), try to set the price to a negative number directly (e.g., `product.realPrice = -50.0;`). See how this is possible and why encapsulation (using a `private` field with a `public` setter method that can validate the input) prevents this kind of error.
-   **AI Learning Prompt:**
    > "I have a `Product` class with private fields. Explain the principle of encapsulation to me like I'm a new programmer. Using my `Product` class as an example, tell me why it's better to use a `setRealPrice(double price)` method instead of making the `realPrice` field public."

### **Inheritance**

-   **Reference:** [Baeldung: Inheritance in Java](https://www.baeldung.com/java-inheritance)
-   **Learning Activity:** Create a new class called `PremiumVIPCustomer` that `extends VIPCustomer`. Don't add any new methods to it. In your `main` method, create an instance of this new class and see what methods are available to you. Notice how it inherits behavior from both `VIPCustomer` and the original `Customer` class.
-   **AI Learning Prompt:**
    > "I have a `Customer` class, which is extended by `VIPCustomer`. Explain the 'is-a' vs. 'has-a' relationship in OOP. Is my `VIPCustomer` a `Customer`, or does it have a `Customer`? Why is inheritance the correct choice here?"

### **Polymorphism**

-   **Reference:** [Oracle's Java Tutorials on Polymorphism](https://docs.oracle.com/javase/tutorial/java/IandI/polymorphism.html)
-   **Learning Activity:** Set a breakpoint in `BillingService` on the line that calls `customer.calculateFinalPrice()`. Run the application in debug mode and step through the code twice: once with a 'Regular' customer and once with a 'VIP' customer. Watch the debugger jump to the correct implementation of the method in the correct class each time.
-   **AI Learning Prompt:**
    > "I have a `Customer` parent class and two child classes, `RegularCustomer` and `VIPCustomer`. My `BillingService` has a variable of type `Customer`. Explain to me, step-by-step, how Java's 'dynamic method dispatch' works to decide which version of the `calculateFinalPrice()` method to run at runtime."

### **Abstraction**

-   **Reference:** [Baeldung: Abstract Classes in Java](https://www.baeldung.com/java-abstract-class)
-   **Learning Activity:** Try to add the line `Customer genericCustomer = new Customer();` in your `BillingService`. Observe the compilation error you get. This demonstrates that you cannot instantiate an abstract class directly.
-   **AI Learning Prompt:**
    > "Explain the concept of an abstract class using my `Customer` class as the primary example. Why would we want to prevent a developer from creating a `new Customer()` object directly? What is the purpose of the `abstract` method `calculateFinalPrice`?"