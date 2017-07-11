(ns ui.docs.buttons
  (:require [ui.elements :as element]
            [ui.layout :as layout]))

(defn documentation []
  [element/article
   "# Button  
   Create Actionable Items of Different Flavours

   "
   [layout/horizontally
    [element/button {:flat? true} "Foo"]
    [element/button {:class "primary"} [:span "Bar"]]
    [element/button {:class "secondary" :rounded? true} "Baz"]
    [element/button {:rounded? true :flat? true} "Qux"]
    [element/button {}
     [element/icon {:font "ion"} "ionic"]]
    [element/button {:flat?     true
                     :circular? true}
     [element/icon {:font "material-icons"} "fingerprint"]]]
   [:p
    [:em
     [:small
      "Note that ui does not come with an icon-library of it's own, so
    you'll need to include one yourself and register it with ui. Have
    a look at the icon-element for further explanation."]]]])

