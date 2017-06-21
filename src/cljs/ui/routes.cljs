(ns ui.routes
  (:require [re-frame.core :as re-frame]
            [secretary.core :as secretary :include-macros true]))

(defn app-routes []

  (secretary/defroute "/" []
    (re-frame/dispatch [:set-active-panel :doc-panel]))

  (secretary/defroute "/:item" [item]
    (re-frame/dispatch [:set-active-doc-item (keyword item)])))