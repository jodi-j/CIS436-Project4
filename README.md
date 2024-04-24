# CIS436 - Project 4: Makeup Match!

Welcome to Makeup Match! Makeup Match is an Android application that allows a user to discover makeup products based on their preferences and add them to their makeup collection. The app is written in Kotlin, utilizes Material Design components using Jetpack recommendations, and implements the Room persistence library in order to deploy an SQLite database. 

The user is able to like or dislike products on the Explore tab, view more details about the products in their collection on the Makeup Collection tab, and set their preferences for products they would like to see on their Explore tab in the Profile tab!

The SQLite database is populated with information from [Makeup API](https://makeup-api.herokuapp.com/). SQLite queries are implemented in the form of data access objects (DAOs) embedded within the code. A navigation bar is used in order navigate between the tabs of the app. The UI and functionality of each tab will be described in detail in the sections below.  

### Tab 1: Explore
The Explore tab shows the user a makeup product that they are allowed to "Like" or "Dislike" by clicking on the corresponding buttons. If the user likes a product, that product is added to their makeup collection. If they dislike a product, then they will be shown another makeup item that they can once again like or dislike. The product name, brand name, and product description are displayed. One fragment, SwipingFragment.kt, that displays all of the relevant information for this tab.

The corresponding widgets and views include:
- Card View that contains a product's information
- Image View Widget (to display product image)
- Text Views that display product information
- Scroll View (for any product descriptions that are lengthy)
- "Like" Button
- "Dislike" Button
  
#### Tab 2: Makeup Collection
The Makeup Collection tab shows the user all of the products that they have liked from the Explore page and have been added into their collection. Each product has a "Details" button that they can click on in order to view the details of that product, which also includes a button that leads the user directly to the website that the product can be purchased on. There are three fragments for this tab. ProductsFragment.kt displays all makeup products in the user's collection, ProductDetailsFragment.kt displays the details for one product, and WebsiteFragment.kt displays the embedded website for the product's purchasing page.

The corresponding widgets and views include: 
**ProductsFragment.kt**
- Card Views that contain each product's information
- Image View Widget (to display product image)
- Text Views that display product information
- "Details" Button that navigates to ProductsDetailsFragment.kt
- Search Bar Widget (not currently functional but will be implemented later on)
**ProductDetailsFragment.kt**
- "Back" Button that navigates back to ProductsFragment.kt
- Scroll View (for all product information)
- Image View Widget (to display product image)
- Horizontal Divider Widget to organize information
- Text Views that display information
- Chip Group to display product tags (if applicable)
- "Link to Product" Button that navigates to WebsiteFragment.kt
**WebsiteFragment.kt**
- "Back" Button that navigates back to ProductDetailsFragment.kt
- Web View Widget that displays product's purchasing page on the brand's website

#### Tab 3: Profile
The Profile tab shows the user any preferences they have selected for the types of products they would like to see on their Explore page. If no preferences are selected, they will be shown any assortment of products. If preferences are selected, they will be displayed on the Profile page and will influence the products shown on the Explore page as well. There is an "Update Preferences" button in order to change their preferences. There are two fragments for this tab: ProfileFragment.kt displays any selected preferences, and UpdatePreferenceFragment.kt displays all available preferences the user can choose from.

The corresponding widgets and views include: 
**ProfileFragment.kt**
- Text Views to display information
- "Update Preferences" button that navigates to UpdatePreferenceFragment.kt
- Chip Group for user's Product preferences (if applicable)
- Chip Group for user's Brand preferences (if applicable)
**UpdatePreferenceFragment.kt**
- Text Views to display information
- Chip Group for all Product preferences
- Chip Group for all Brand preferences
- Scroll View to contain all chips
- "Submit Updates" button that updates preferences and navigates back to ProfileFragment.kt

We were not able to implement all of our desired features, so these are some features that we would like to implement for Makeup Match in the future!
- Implement login information for multiple users!
- Add a color scheme.
- Add app animations/transitions between activity/view/fragment switches.
- Implement search bar functionality in the Makeup Collection tab.
