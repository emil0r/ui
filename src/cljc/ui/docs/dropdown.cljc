(ns ui.docs.dropdown
  (:require [ui.elements :as element]
            [ui.layout :as layout]
            #?(:cljs [reagent.core :refer [atom]])
            [ui.util :as util]))

(defn documentation []
  (let [!open? (atom false)]
    (fn []
      [element/article
       "### Dropdown
       "
       [layout/vertically
        [element/button {:class "secondary"
                         :on-click #(reset! !open? (not @!open?))}
         [element/icon {:font "ion"} (str "android-arrow-drop" (if @!open? "up" "down"))]
         "Open"]
        [element/dropdown {:open? @!open?
                           :origin [:top :right]
                           :style {:width "360px"}}
         [layout/horizontally [:h5 "Notifications"]]
         [layout/horizontally {:style {:border-top "1px solid rgb(230,230,230)"
                                       :background "rgb(250,250,250)"}
                               :space :around
                               :fill? true}
          [element/icon {:font "ion"
                         :size 2} "ios-information-outline"]
          [:small (str "Some information for you")]
          [:small (str "1 min ago")]]]]])))