# Billing System (Java Swing)

A simple shop billing program made in Java Swing for my Information Technology project (Class XII).
You pick the items, type the quantity, and the program works out the sub total, discount, GST and the
grand total. The bill is shown on the screen and can be saved as a text file.

![Bill generated](screenshots/bill-generated.png)

## Features

- 15 shop items in a drop-down list, price comes automatically
- Add the same item again and its quantity increases (no duplicate rows)
- Remove any item from the bill
- Discount (%) and GST (5%) calculated instantly, even while typing the discount
- Bill number (starts from 1001) and date/time filled automatically
- Input checking for name, mobile number (10 digits), quantity and discount
- Bill preview in proper bill format
- **Save Bill** writes the bill to `bills/Bill_<number>.txt`
- Clear button for a new bill, Close button that asks before exiting

## How to run

You need the **JDK 8 or higher** installed (check with `java -version` and `javac -version`).

```bash
git clone https://github.com/<your-username>/<your-repo-name>.git
cd <your-repo-name>

javac BillingSystem.java
java BillingSystem
```

With JDK 11 or newer you can also run it directly:

```bash
java BillingSystem.java
```

It also works in NetBeans, Eclipse, IntelliJ or VS Code - just open `BillingSystem.java` and run it.

> **Note:** this is a desktop (Swing) application, so it opens a window on your computer.
> It cannot run inside the browser or on GitHub Pages.

## How the calculation is done

```
Sub Total   = sum of (price x quantity)
Discount    = Sub Total x Discount% / 100
GST         = (Sub Total - Discount) x 5 / 100
Grand Total = (Sub Total - Discount) + GST
```

Example: Sub Total 866.00, 10% discount = 86.60, GST = 38.97, **Grand Total = 818.37**

## Screenshots

| Start screen | Items added |
|---|---|
| ![Start](screenshots/form-empty.png) | ![Items](screenshots/items-added.png) |

| Bill saved | Wrong input |
|---|---|
| ![Saved](screenshots/bill-saved.png) | ![Error](screenshots/error-mobile.png) |

(The screenshots were taken on a Linux machine, so fonts and window borders will look a little different on Windows.)

## Project files

```
BillingSystem.java   the complete program (one file)
screenshots/         pictures used in this README
docs/                project file (PDF) submitted at school
```

## Changing the items

The items and prices are kept in two arrays at the top of `BillingSystem.java`
(`itemNames` and `itemPrices`). Add a name in the first array and its price at the
same position in the second array. If you change the GST percentage, change `GST_RATE`
(and the label "GST @ 5%").

## Made by

Suhani Arora, Class XII-H, Bal Mandir Sr. Sec. School
Partners: Ruhman, Daksh
