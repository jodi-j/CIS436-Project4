# CIS436 - Project 4: Makeup Tinder!

### Notes:
- Material Design components will be used to satisfy Jetpack requirements
- Navigation bar for navigating between fragments
- 3 total fragments
- come up with a cute name for the app
- handle app animations once all functionality is finished
- choose color scheme

### Fragment Requirements
#### Fragment 1: Swiping on Makeup Products
- Display information about product, such as product type, price, brand, rating, and relevant tags
- Image View Widget (to display product image)
- Button to "swipe left" and dislike the product
- Button to "swipe right" and like product, adding it to user's collection on **Fragment 2**
  
#### Fragment 2: Makeup Collection
- Display all products that user has liked
- SQLite database to hold all products that user has liked
- Search Bar Widget (to allow user to search all products in collection)
- *OPTIONAL*: Web View Widget (to allow user to access purchase link for product directly through app)
- *OPTIONAL*: add a rating to the product using RatingBar widget

#### Fragment 3: User Profile
- Display user information and set preferences for products using preference quiz
  - filters can include: brand, product type, relevant tags (vegan, hypoallergenic, organic, etc.)
- Progress Bar Widget (to show user's progress through preference quiz)
- Selected filters will influence products shown on **Fragment 1**
- *OPTIONAL*: implement different user logins using SQLite database

### Links:
- Makeup API: https://makeup-api.herokuapp.com/
- Final Report: https://docs.google.com/document/d/1uS6o1IwYwTDymhZzJgH-qKUBv0HJmROQB1ZA5tAXtaU/edit?usp=sharing
