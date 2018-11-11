# Application Analyzer

The project is the part of the master thesis "Security Reputation System for Android Applications".

The software includes the following features:

* Generates overall **offline** and **online** trust to each application based on
gathered static data and parsed **Google Play** data
* Obtains **installed applications list**
* Obtains **static system information** about each application after clicking on
it
* Parses data be means of **jsoup** library from Google Play which constitutes
offline and online trust calculation trust using AsyncTask
* Calculates the number of **dangerous permissions** along with **permission
groups**
* Implements expandable permissions list for each application
* Displays three categories: **System Data**, **Google Play**, **Permissions** by
means of Sliding Tabs
* **Firebase Analytics** and **Google AdMob** is involved
* Is solely written in the **Java Programming Language**
* Keeps all strings in a strings.xml file and enables **RTL layout**
switching on all layouts
* Includes support for accessibility
* Utilizes stable libraries versions

Libraries Used:
* **Jsoup** — parsing Google Play data
* **Butterknife** — casting views
* **Espresso** — establishing UI tests

![Alt Text](https://im4.ezgif.com/tmp/ezgif-4-59a932f0c322.gif)
