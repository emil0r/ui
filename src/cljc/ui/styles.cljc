(ns ui.styles
  (:refer-clojure :exclude [-])
  #?(:cljs (:require-macros [garden.def :refer [defcssfn defkeyframes]]))
  (:require #?(:clj [garden.def :refer [defcssfn defkeyframes]])
            [garden.arithmetic :refer [-]]
            [garden.units :as u]
            [garden.color :as color]
            [garden.selectors :as s]
            [ui.element.clamp :as clamp]
            [ui.element.progress-bar :as progress-bar]))


;;
;; Realizations that should be materialized
;;
;; - Many layout-problems arise from the fact that we're mixing
;;   text-layouts with structure. I think we would be well
;;   served by resetting everything quite strictly, so that line-height
;;   etc are extremely uniform, then have a text-content box with all
;;   typographic styles applied. This is how it works on most other
;;   platforms; say "wonder why?" ;)
;;
;; - Styles should be moved closer to it's element, preferably in the
;;   same file as it's views. I think going even one step further and
;;   having most of it scoped to it's element could be useful. Knowing
;;   that those styles will not bleed into other areas would be super
;;   useful.
;;
;; - Animations should be made a first-class citizen of this library.
;;   Strategy for how containers will melt together or spread apart,
;;   how they push and pull each other and so forth. This also means
;;   that most elements will need to be created from the same cloth
;;   / "container".
;;


(defcssfn cubic-bezier)
(defcssfn linear-gradient)
(defcssfn rotateZ)
(defcssfn scale)
(defcssfn translateX)
(defcssfn translateY)
(defcssfn translateZ)


(defn dark?
  "Is the [r g b]-color supplied a dark color?"
  [[r g b]]
  (> (- 1 (/ (+ (* 0.299 r)
                (* 0.587 g)
                (* 0.114 b)) 255)) 0.5))


(defn gray
  "Creates a [shade] of gray"
  [shade]
  (apply color/rgb (take 3 (repeat shade))))


(def theme
  {:default {:background (color/rgb 245 245 245)
             :primary (color/rgb 96 191 253)
             :secondary (color/rgb 90 100 110)
             :positive (color/rgb 34 192 100)
             :negative (color/rgb 232 83 73)
             :font-weight 100
             :font-scale (u/rem 1.8)}})


(defn- golden [x] (* (/ x 16) 9))


(defn structure [theme]
  [[:body {:overflow :hidden}]
   ;; TODO #app:first-child, really!?
   [:html :body :#app :#app:first-child {:height (u/percent 100)
                                         :width  (u/percent 100)}]
   [:html :body :menu :ul {:margin  0
                           :padding 0}]
   [:main {:height "calc(100vh - 64px)"}]
   [:.Vertical-rule :.Horizontal-rule {:background-color :silver}]
   [:.Vertical-rule {:width            (u/px 1)
                     :min-height       (u/rem 2.5)
                     :position         :relative
                     :left             (u/rem 3)
                     :margin-right     (u/rem 6)
                     :height           (u/percent 100)}]
   [:.Horizontal-rule {:width (u/percent 100)
                       :height (u/px 1)
                       :position :relative
                       :top (u/rem 3)
                       :margin-bottom (u/rem 6)}]
   [:article {:padding (u/rem 4)
              :text-align :left}
    [:section {:max-width (u/rem 70)
               :margin-right (u/rem 3)}]]])


(defn layouts [{:keys [background primary]}]
  [[:.Backdrop {:background (color/rgba 0 0 1 0.5)
                :width      (u/percent 100)
                :height     (u/percent 100)
                :position   :absolute
                :left       0
                :top        0
                :transition [[:opacity :500ms :ease]]
                :opacity    0
                :z-index    -1}]
   [(s/> :.Sidebar.Locked.Align-left :.Slider :main) {:margin-left (u/px 360)}]
   [:.Sidebar {:overflow :hidden
               :width    (u/percent 100)
               :height   (u/percent 100)}
    [:.Slider {:width          (u/percent 100)
               :height         (u/percent 100)
               :position       :relative}]
    [:sidebar {:width       (u/px 360)
               :background  background
               :height      (u/percent 100)
               :position    :absolute
               :top 0
               :z-index     9
               :line-height (u/rem 3)}]
    [:main {:width              (u/percent 100)
            :overflow           :auto
            :overflow-scrolling :touch
            :position           :relative
            :height             (u/percent 100)}]
    [:&.Locked
     [:main {:width "calc(100% - 360px)"}]]
    [(s/& (s/not :.Locked))
     [:.Slider {:transition [[:500ms :ease]]}]
     [:&.Align-left
      [:sidebar {:left (u/px -360)}]]
     [:&.Align-right
      [:sidebar {:right (u/px -360)}]]
     [:&.Ontop
      [:sidebar {:transition [[:500ms :ease]]}]]
     [:&.Open
      [:.Backdrop {:opacity 1
                   :z-index 8}]
      [(s/& (s/not :.Ontop))
       [:&.Align-left [:.Slider {:transform (translateX (u/px 360))}]]
       [:&.Align-right [:.Slider {:transform (translateX (u/px -360))}]]]
      [:&.Ontop
       [:sidebar {:box-shadow [[0 0 (u/rem 6) (color/rgba 0 0 1 0.5)]]}]
       [:&.Align-left [:sidebar {:transform (translateX (u/px 360))}]]
       [:&.Align-right [:sidebar {:transform (translateX (u/px -360))}]]]]]]])


(defn header [theme]
  [[:.Header :.Card {:background-color :white
                     :box-shadow       [[0 (u/rem 0.2) (u/rem 0.3) (color/rgba 35 35 35 0.2)]]}]
   [:.Header {:justify-content :space-between
              :align-items     :center
              :width           (u/vw 100)
              :box-sizing      :border-box
              :padding         [[0 (u/rem 1)]]
              :position        :relative}
    [:&.Small {:flex    [[0 0 (u/px 64)]]
               :height  (u/px 64)
               :z-index 7}]
    [:&.Large {:flex    [[0 0 (u/px 128)]]
               :height  (u/px 128)
               :z-index 7}]
    [:img {:max-height    (u/px 48)
           :border-radius (u/percent 50)}]]])


(defn card [theme]
  [[:.Card {:border-radius   (u/rem 0.3)
            :overflow   :hidden
            ;; :margin        (u/rem 1)
            :min-width  (u/rem 26)
            :min-height (u/rem (golden 26))}]])


(defn container [theme]
  [[:.Flex {:flex 1}]
   [".Container:not(.Vertically):not(.Compact) > * + *" {:margin-left (u/rem 1)}]
   [".Container.Vertically:not(.Compact) > * + *" {:margin-top (u/rem 1)}]
   [:.Container {:box-sizing       :border-box
                 :flex-grow        1
                 :flex-shrink      1
                 :flex             1
                 :-webkit-box-flex 1
                 :-ms-flex         [[0 1 :auto]]}
    ^:prefix {:display :flex}
    [:&.Hide {:display :none}]
    [(s/& (s/not :Vertically)) {:flex-direction :row}]
    [(s/& (s/not :.No-wrap)) {:flex-wrap :wrap}]
    [(s/& (s/not :.No-gap)) {:padding (u/rem 2)}]
    [(s/& (s/not (s/attr-contains :class "Align"))) {:align-items :flex-start}]
    [(s/& (s/not (s/attr-contains :class "Justify"))) {:justify-content :flex-start}]
    [:&.Timeline {:border-bottom [[:solid (u/px 1) (color/rgb 190 190 190)]]
                  :height        (u/px 100)
                  :flex-grow     0}
     [:.Month {:border-right [[:solid (u/px 1) (color/rgb 190 190 190)]]}]
     [:.Day {:border-left [[:solid (u/px 1) (color/rgb 190 190 190)]]
             :height      (u/px 20)}]]
    [:&.Vertically {:flex-direction :column}]
    [:&.Align-start {:align-items :flex-start}]
    [:&.Align-end {:align-items :flex-end}]
    [:&.Align-stretch {:align-items :stretch}]
    [:&.Align-center {:align-items :center}]
    [:&.Justify-space-around {:justify-content :space-around}]
    [:&.Justify-space-between {:justify-content :space-between}]
    [:&.Justify-center {:justify-content :center}]
    [:&.Justify-start {:justify-content :flex-start}]
    [:&.Justify-end {:justify-content :flex-end}]
    ;; TODO https://github.com/noprompt/garden/issues/127
    ;; [(s/& :.Container (s/> (s/not :.Compact) (s/+ :* :*))) {:margin-left (u/rem 2)}]
    [:&.Fill :.Fill {:box-sizing :border-box
                     :flex       1
                     :min-width  0
                     :min-height 0
                     :height     (u/percent 100)
                     :width      (u/percent 100)}]
    [:&.Rounded {:border-radius (u/rem 0.2)}]
    [:&.Raised {:box-shadow [[0 (u/rem 0.2) (u/rem 0.2) (color/rgba 0 0 1 0.3)]]}]]])


(defn containers [theme]
  [(container theme)
   (header theme)
   (card theme)
   [:.Hide {:display :none}]
   [:.Timeline {:width :auto :position :absolute}
    [".Button:not(.Flat) + .Button:not(.Flat)" {:border-bottom-left-radius 0
                                                :border-top-left-radius    0
                                                :position                  :relative}
     [:&:before {:content    "' '"
                 :display    :block
                 :width      (u/rem 2.5)
                 :height     (u/rem 3.7)
                 :position   :absolute
                 :top        (u/rem -0.1)
                 :left       (u/rem -2.5)
                 :background :inherit}]]]
   [:.Timeline-wrapper {:width       (u/percent 100)
                        :height      :auto
                        :user-select :none
                        :position    :relative
                        :overflow    :hidden}]])


(defn- typography [{:keys   [font-scale font-weight]
                :or {font-scale (u/em 1.8)
                     font-weight 100}}]
  [[:html {:font-size   (u/percent 62.5)
           :font-weight font-weight
           :font-family [:Roboto [:Helvetica :Neue] :Helvetica]}]
   [:body :input {:font-size font-scale}]
   [:h1 :h2 :h3 :h4 :h5 :h6 {:font-weight :normal}]
   [:p {:line-height (u/em 1.55) :margin-bottom (u/em 3.1)}]
   [:.Copy {:max-width (u/rem 65)}]
   [:.Newspaper {:text-align :justify}]
   [:.Legal {:font-size (u/em 0.7)}
    [(s/> :*) {:margin-right (u/rem 1)}]]])


(defkeyframes pulse-color
  [:from {:background (gray 240)}]
  [:to {:background (gray 220)}])


(defn animations [theme]
  [pulse-color])


(defn- forms [{:keys [primary secondary]}]
  [[:.Auto-complete {:position      :relative
                     :width         (u/percent 100)
                     :margin-bottom (u/rem 1)}
    [:&.Read-only [:* {:cursor :default}]]
    [:.Textfield {:margin-bottom 0}]]
   [:.Labels
    [:.Label:first-child {:margin-left 0}]
    [:.Label:last-child {:margin-right 0}]]
   [:.Label
    [:input {:position :absolute
             :left     (u/percent -100)
             :z-index  -10}]
    [(s/+ (s/input (s/focus)) :label) {:opacity 1}]
    [:label {:background    (color/lighten primary 15)
             :color         (color/darken primary 40)
             :display       :inline-block
             :padding       [[(u/rem 0.5) (u/rem 1)]]
             :margin        [[(u/rem 0.5) (u/rem 1)]]
             :opacity       0.4
             :border-radius (u/rem 0.4)
             :font-size     (u/em 0.75)
             :position      :relative
             :z-index       1
             :user-select   :none
             :cursor        :pointer
             :margin-right  (u/rem 0.5)}]]
   [:.Textfield {:position :relative
                 :margin   [[(u/rem 3) 0 (u/rem 1)]]}
    [:&.Dirty
     [:label {:left             0
              :transform        [[(translateY (u/percent -100)) (scale 0.75)]]
              :transform-origin [[:top :left]]}]]
    [:&.Read-only [:* {:cursor :pointer}]]
    [:&.Disabled [:* {:cursor :not-allowed}]
     [:label {:color :silver}]]
    [(s/> :input) {:box-sizing :border-box
                   :margin     0
                   :display    :inline-block
                   :width      (u/percent 100)}]
    [:label {:position   :absolute
             :color      :silver
             :transition [[:all :200ms :ease]]
             :transform  (translateZ 0)
             :left       0
             :cursor     :text
             :top        (u/rem 0.5)
             :z-index    1}]
    [:input {:background    :transparent
             :border        :none
             :border-bottom [[(u/px 1) :solid :silver]]
             :display       :block
             :transition    [[:all :200ms :ease]]
             :font-weight   :600
             :outline       :none
             :padding       [[(u/rem 0.5) 0]]
             :position      :relative}
     [:&:focus {:border-color primary}
      [:+ [:label {:color            :black
                   :left             0
                   :transform        [[(translateY (u/percent -100)) (scale 0.75)]]
                   :transform-origin [[:top :left]]}]]]
     [:&:disabled {:cursor :not-allowed}]
     [:&:required
      [:&.invalid {:border-color :red}]]]
    [:.Ghost {:color    (color/rgba 0 0 1 0.3)
              :position :absolute
              :top      (u/rem 0.5)}]]
   [:.Collection {:background         (color/rgba 254 254 255 0.85)
                  :box-shadow         [[0 (u/rem 0.1) (u/rem 0.2) (color/rgba 0 0 1 0.2)]]
                  :max-height         (u/rem 30)
                  :position           :absolute
                  :width              (u/percent 100)
                  :overflow-scrolling :touch
                  :overflow           :auto
                  :list-style         :none
                  :padding            0
                  :z-index            2}
    [(s/> :li) {:border-bottom [[:solid (u/rem 0.1) (color/rgba 150 150 150 0.1)]]
                :padding       [[(u/rem 1) (u/rem 2)]]}
     [:&.Selected {:background (color/rgba 0 0 1 0.02)}]
     [:&:hover {:background-color (color/rgba 200 200 200 0.1)}]
     [(s/& (s/last-child)) {:border-bottom :none}]]]])


(defn calendar [{:keys [primary secondary]}]
  [[:.Date-picker {:position :relative
                   :width    (u/percent 100)}
    [:.Calendar {:background :white
                 :z-index    3}]]
   [:.Calendar {:user-select  :none
                :text-align   :center
                :table-layout :fixed
                :width        (u/percent 100)}
    [:th {:padding (u/rem 2)}]
    [:td {:padding (u/rem 1)}]
    [:.Previous :.Next {:color (color/lighten secondary 50)}]
    [:.Day {:pointer :not-allowed}
     [:&.Selectable {:cursor :pointer}
      [:&:hover [:span {:background (color/lighten primary 30)}]]]
     [:span {:border-radius (u/percent 50)
             :display       :inline-block
             :padding       (u/rem 1)
             :height        (u/rem 2)
             :width         (u/rem 2)}]
     [:&.Today
      [(s/> :span) {:border [[:solid (u/px 1) (color/darken primary 10)]]}]]
     [:&.Selected.Selectable {:color  :white
                              :cursor :default}
      [(s/> :span) {:background primary}]]]]])


(defn numbers [theme]
  [[:.Worksheet {:width       (u/percent 100)
                 :user-select :none}
    [:.Table {:border-bottom [[:solid (u/px 1) (gray 230)]]
              :width         (u/percent 100)}]
    [:.Arrow {:color         (gray 150)
              :padding-left  (u/rem 1)
              :padding-right (u/rem 1)}]
    [:.Column-headings {:background :white}
     [:th :td {:border-top [[:solid (u/px 1) (gray 230)]]}]]
    [:.Column-heading {:position :relative}
     [:.Dropdown-origin {:opacity    0
                         :transform  [[(translateY (u/percent -50)) (rotateZ (u/deg 90))]]
                         :transition [[:200ms :ease]]
                         :cursor     :pointer
                         :outline    :none
                         :border     :none
                         :background :transparent
                         :color      (gray 130)
                         :font-size  (u/rem 1.5)
                         :position   :absolute
                         :right      (u/rem 1)
                         :top        (u/percent 50)}]
     [:&:hover
      [:.Dropdown-origin {:opacity 1}]]]
    [:tr
     [:td:first-child :th:first-child {:border-left [[:solid (u/px 1) (gray 230)]]}]]
    [:&.Editable
     [:.Cell {:cursor :cell}]]
    [:.Duplicate {:position :relative}
     [:&:before {:content      "' '"
                 :box-sizing   :content-box
                 :display      :block
                 :border-style :solid
                 :border-color (color/rgb 235 200 0)
                 :border-width [[0 (u/px 1)]]
                 ;; :box-shadow [[:inset 0 0 (u/em 0.1) (color/rgb 245 228 90)] [0 0 (u/em 0.1) (color/rgb 245 228 90)]]
                 :width        (u/percent 100)
                 :height       (u/percent 100)
                 :top          (u/px -1)
                 :left         (u/px -1)
                 :position     :absolute}]
     [:&.First
      [:&:before {:border-top [[:solid (u/px 1) (color/rgb 235 200 0)]]}]]
     [:&.Last
      [:&:before {:border-bottom [[:solid (u/px 1) (color/rgb 235 200 0)]]}]]]
    [:th
     [:span {:display :inline-block}]]
    [:td
     [:span {:display :block}]]
    [:td :th
     [:span {:overflow      :hidden
             :white-space   :nowrap
             :text-overflow :ellipsis}]
     [:&.Number {:text-align :right}]
     [:&.Index :&.Alpha {:font-size   (u/em 0.7)
                         :font-weight 100}]
     [:&.Smaller {:font-size (u/em 0.45)}]
     [:&.Index :&.Select :&.Alpha {:text-align :center}]]
    [:.Editable
     [:&:hover {:position :relative}
      [:&:before {:content    "' '"
                  :box-sizing :content-box
                  :display    :block
                  :border     [[:solid (u/px 1) (gray 150)]]
                  :width      (u/percent 100)
                  :height     (u/percent 100)
                  :top        (u/px -1)
                  :left       (u/px -1)
                  :position   :absolute}]
      [:&:after {:content       "' '"
                 :border-radius (u/percent 50)
                 :border        [[:solid (u/px 1) (gray 150)]]
                 :display       :block
                 :position      :absolute
                 :bottom        0
                 :left          (u/percent 50)
                 :cursor        :row-resize
                 :transform     [[(translateX (u/percent -50)) (translateY (u/percent 50))]]
                 :height        (u/em 0.4)
                 :width         (u/em 0.4)
                 :background    (color/rgb 245 228 90)}]]]
    [:.Headers {:width (u/percent 100)}]
    [:.Body {:max-height (u/vh 70)
             :background :white
             :width      (u/percent 100)
             :overflow   :auto}
     [:tr:last-child
      [:td {:border-bottom 0}]]]
    [:table {:border-collapse :separate
             :table-layout    :fixed
             :width           :inherit}]
    [:.Titlecolumn {:background-color (gray 245)
                    :text-align       :left}]
    [:&.Selectable
     [:tr
      [:&:hover
       [:td {:background-color (gray 245)}]]
      [:&.Selected
       [:td :th {:background-color (gray 245)}]]]]
    [:th :td {:border-bottom [[:solid (u/px 1) (gray 230)]]
              :border-right  [[:solid (u/px 1) (gray 230)]]
              :padding       (u/rem 1)}]


    [:.Auto-complete {:margin 0}
     [:span {:display :inline}]
     [:.Collection {:background :white
                    :border     [[:solid (u/px 1) :silver]]}]
     [:.Textfield {:margin  0
                   :padding 0}
      [:input
       [:&:focus
        [:+ [:label {:opacity 0}]]]]
      [:input {:border :none}]]]]])


(defn color-picker [theme]
  (let [swatch-size {:height (u/em 5.625)
                     :width  (u/em 10)}]
    [[".Swatch::-webkit-color-swatch-wrapper" {:padding 0}]
     [".Swatch::-webkit-color-swatch" {:border 0}]
     [:.Swatch {:width         (u/em 4)
                :height        (u/em 4)
                :border-radius (u/percent 50)}]
     [:.Color-picker {:background    :white
                      :display       :inline-block
                      :border-radius (u/rem 0.25)
                      :overflow      :hidden
                      :text-align    :center}
      [:.Swatch (merge {:margin             [[0 :auto]]
                        :padding            0
                        :position           :relative
                        :-webkit-appearance :none
                        :border             0
                        :border-radius      0
                        :outline            :none
                        :overflow           :hidden} swatch-size)]
      [:.Value {:font-size (u/em 0.7)
                :cursor    :pointer
                :padding   (u/em 0.5)}]]]))


(defn- buttons [{:keys [primary secondary positive negative]}]
  [[:.Button {:appearance     :none
              :background     (gray 230)
              :border-radius  (u/em 0.2)
              :border         [[:solid (u/em 0.1) (gray 215)]]
              :outline        :none
              :user-select    :none
              :min-width      (u/rem 8)
              :text-transform :uppercase
              :transition     [[:background-color :200ms :ease]]
              :padding        [[(u/em 1) (u/em 2)]]}
    [:&:hover {:background-color (gray 240)}]
    [:&.Secondary {:background-color secondary
                   :border-color     secondary
                   :color            (if (dark? [(-> secondary :red)
                                                 (-> secondary :blue)
                                                 (-> secondary :green)]) :white :black) }
     [:&:hover {:background-color (color/lighten secondary 10)}]]
    [:&.Primary {:background-color primary
                 :border-color     primary
                 :color            (if (dark? [(-> primary :red)
                                               (-> primary :blue)
                                               (-> primary :green)]) :white :black) }
     [:&:hover {:background-color (color/lighten primary 10)}]]
    [:&.Icon {:padding [[(u/em 0.7) (u/em 2)]]}
     [:i {:font-size (u/em 1.5)}]]
    [:&.No-chrome {:border-color     :transparent
                   :background-color :transparent}]
    [(s/& (s/not :disabled)) {:cursor :pointer}]
    [:&.Positive {:background-color positive
                  :border-color     positive}]
    [:&.Negative {:background-color negative
                  :border-color     negative}]
    [:&.Flat {:background-color :transparent
              :border           [[:solid (u/em 0.1) :inherit]]}]
    [:.Rounded {:border-radius (u/em 2)}]]
   [:.Dropdown {:position         :absolute
                :background       (gray 255)
                :border           [[:solid (u/px 1) (gray 220)]]
                :box-sizing       :border-box
                :transform        [[(translateY (u/percent 100)) (scale 1)]]
                :transform-origin [[:top :right]]
                :transition       [[:200ms (cubic-bezier 0.770, 0.000, 0.175, 1.000)]]
                :bottom           0
                :right            0
                :z-index          10
                :max-height       (u/rem 40)
                :overflow         :auto
                :width            (u/percent 100)
                :max-width        (u/rem 25)}
    [(s/& (s/not :.Open)) {:transform [[(translateY (u/percent 100)) (scale 0)]]}]
    [:.Row {:text-align    :left
            :margin-top    (u/em 0.5)
            :margin-bottom (u/em 0.5)
            :width         (u/percent 100)
            :overflow      :hidden
            :white-space   :nowrap
            :text-overflow :ellipsis}
     [:label {:display :inline-block
              :width   (u/percent 100)}]]
    [:.Button {:border-radius  0
               :border-left    0
               :border-right   0
               :border-top     0
               :margin         0
               :text-align     :left
               :text-transform :none
               :width          (u/percent 100)}]]])


(defn- tmp [{:keys [primary secondary background positive negative]}]
  [
   [:.Code :pre {:background    (gray 250)
                 :border-radius (u/rem 0.3)
                 :color         (gray 170)
                 :font-family   [[:monospace]]
                 :white-space   :pre
                 :padding       (u/rem 2)}]
   [:.Code :code {:line-height 1.8}
    [:label {:background    (color/rgb 38 189 230)
             :color         :white
             :border-radius (u/rem 1)
             :padding       [[(u/rem 0.2) (u/rem 0.5)]]}]]
   [:.Keyword {:color (color/rgb 60 140 180)}]
   [:.Symbol {:color (color/rgb 60 220 190)}]
   [:.Parens {:color (color/rgb 180 70 200)}]

   [:.Dialog {:position :fixed
              :left     0
              :top      0
              :height   (u/percent 100)
              :width    (u/percent 100)
              :margin   [[0 :!important]]
              :z-index  10}
    [(s/& (s/not :.Open)) {:display :none}]
    [:&.Open
     [:.Backdrop {:opacity 1
                  :z-index 10}]]]
   [:.Dialog-content {:background    :white
                      :border-radius (u/rem 0.8)
                      :position      :absolute
                      :left          (u/percent 50)
                      :top           (u/percent 50)
                      :transform     [[(translateY (u/percent -50)) (translateX (u/percent -50))]]
                      :z-index       12}]
   [:.Shape {:display        :inline-flex
             :position       :relative
             :flex-direction :column}]
   [:.Checkbox :.Toggle
    [:input {:-webkit-appearance :none
             :background         :white
             :border             [[:solid (u/px 1) :silver]]
             :outline            :none}]]
   [:.Radio
    [:.Shape {:border-radius (u/percent 50)}]]
   [:.Toggle
    [:.Shape {:width        (u/rem 4)
              :margin-right (u/rem 1)}]
    [:input {:border-radius (u/rem 1.2)
             :background    (linear-gradient (u/deg 45) (color/rgba 0 0 1 0.2) (color/rgba 0 0 1 0.1))
             :position      :absolute
             :transition    [[:200ms :ease]]
             :top           0
             :margin        0
             :height        (u/percent 100)
             :width         (u/percent 100)
             :z-index       1}]
    [:i {:background      :white
         :box-shadow      [[0 (u/px 1) (u/px 2) (color/rgba 0 0 1 0.5)]]
         :display         :flex
         :margin          (u/rem 0.1)
         :align-items     :center
         :justify-content :center
         :padding         (u/rem 0.5)
         :align-self      :flex-start
         :position        :relative
         :transition      [[:200ms :ease]]
         :line-height     0
         :height          (u/rem 1)
         :width           (u/rem 1)
         :z-index         2
         :border-radius   (u/percent 50)}]
    [:&.Checked
     [:i {:transform (translateX (u/rem 1.75))}]
     [:input {:background   (linear-gradient (u/deg 45) (color/lighten positive 5) (color/darken positive 5))
              :border-color (color/darken positive 10)}]]]
   [:.Checkbox {:display     :flex
                :user-select :none
                :position    :relative}
    [:.Shape {:margin-right (u/rem 0.5)}]
    [:input {:position :relative}]
    [:i {:position         :absolute
         :left             (u/percent 50)
         :top              (u/rem -0.3)
         :font-size        (u/rem 2.5)
         :transform-origin [[:center :center]]
         :transform        [[(scale 0) (translateX (u/percent -50))]]
         :transition       [[:100ms :ease]]
         :z-index          2}]
    [:&.Checked
     [:i {:transform [[(scale 1) (translateX (u/percent -50))]]}]
     [:input {:background   primary
              :border-color (color/darken primary 10)}]]
    [:input {:-webkit-appearance :none
             :background         :white
             :border             [[:solid (u/px 1) :silver]]
             :position           :relative
             :outline            :none
             :transition         [[:100ms :ease]]
             :width              (u/rem 1.5)
             :height             (u/rem 1.5)
             :border-radius      (u/rem 0.2)
             :z-index            1}]]
   [:body {:background-color background}]
   [:menu [:a {:display :block}]]
   [:a {:color           secondary ; primary
        :text-decoration :none}
    [:&.Primary {:color primary}]
    [:&:hover {:color (color/darken secondary 30)}
     [:&.Primary {:color primary}]]]])


(def docs
  (let [contrast (gray 240)
        triangle (linear-gradient (u/deg 45)
                                  [contrast (u/percent 25)]
                                  [:transparent (u/percent 25)]
                                  [:transparent (u/percent 75)]
                                  [contrast (u/percent 75)]
                                  contrast)]
    (list
     [:.Container
      [:&.Demo {:background          (repeat 2 triangle)
                :background-position [[0 0] [(u/px 10) (u/px 10)]]
                :background-size     [[(u/px 20) (u/px 20)]]
                :min-width           (u/vw 50)
                :min-height          (u/vh 35)
                :box-shadow          [[:inset 0 (u/px 2) (u/px 8) (color/rgba 0 0 1 0.3)]]}
       [:&.Horizontally
        [:&.Align-left {:align-items :flex-start}]
        [:&.Align-right {:justify-content :flex-end}]]
       [:&.Vertically
        [:&.Align-top {:align-items :flex-start}]
        [:&.Align-bottom {:justify-content :flex-end}]]
       [:.Fill {:background (-> theme :default :primary)
                :border     [[:dashed (u/px 1) (color/rgba 0 0 1 0.2)]]}]]]
     [:.Demo-box {:border           [[:solid (u/px 1) :silver]]
                  :background-color :white
                  :padding          (u/rem 2)}]
     [:body {:background :white}]
     [:.Functional-hide {:position :absolute
                         :left     (u/vw -200)}]
     [:.Sidebar {:background 245 245 245}
      ;; [:sidebar
      ;;  [:a {:color (-> theme :default :secondary)}]]
      ])))


(def screen
  (let [theme (:default theme)]
    (list (structure theme)
          (layouts theme)
          (containers theme)
          (animations theme)
          (numbers theme)
          (forms theme)
          (color-picker theme)
          (calendar theme)
          (typography theme)
          (buttons theme)
          (tmp theme)
          (clamp/style theme)
          (progress-bar/style theme))))