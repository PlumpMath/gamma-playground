(ns ^:figwheel-always gampg.core
    (:require [clojure.string :as string]
              [fipp.clojure :as fipp-code]
              [fipp.edn :as fipp]
              [gampg.learn-gamma.lesson-01 :as lg01]
              [gampg.learn-gamma.lesson-02 :as lg02]
              [gampg.learn-gamma.lesson-03 :as lg03]
              [gampg.learn-gamma.lesson-04 :as lg04]
              [gampg.learn-gamma.lesson-05 :as lg05]
              [gampg.learn-gamma.lesson-06 :as lg06]
              [gampg.learn-gamma.lesson-07 :as lg07]
              [gampg.learn-gamma.lesson-08 :as lg08]
              [gampg.learn-gamma.lesson-09 :as lg09]
              [gampg.learn-gamma.lesson-10 :as lg10]
              [gampg.learn-gamma.lesson-11 :as lg11]
              [gampg.learn-gamma.lesson-12 :as lg12]
              [gampg.learn-gamma.lesson-13 :as lg13]
              [gampg.learn-gamma.lesson-14 :as lg14]
              [gampg.learn-gamma.lesson-15 :as lg15]
              [gampg.learn-gamma.lesson-16 :as lg16]
              [gampg.learn-gamma.lesson-17 :as lg17]
              [gampg.learn-gamma.lesson-18 :as lg18]
              [gampg.learn-gamma.lesson-19 :as lg19]
              [gampg.learn-gamma.lesson-20 :as lg20]
              [gampg.learn-gamma.apartment :as lg-apartment]
              [gampg.learn-gamma.apartment-vr :as lg-vr]
              [gampg.learn-gamma.gltf :as lg-gltf]
              [gampg.learn-gamma.lightmap :as lg-lightmap]
              [markdown.core :as md]
              [om.core :as om :include-macros true]
              [om.dom :as dom :include-macros true]))

(enable-console-print!)

;; TODO: Add a menu for selecting examples
(def lessons
  [lg01/summary
   lg02/summary
   lg03/summary
   lg04/summary
   lg05/summary
   lg06/summary
   lg07/summary
   lg08/summary
   lg09/summary
   lg10/summary
   lg11/summary
   lg12/summary
   lg13/summary
   lg14/summary
   lg15/summary
   lg16/summary
   lg17/summary
   lg18/summary
   lg19/summary
   lg20/summary
   ])

(defonce app-state
  (atom {:current-lesson {:index 19
                          :exit  nil
                          :enter nil}}))

#_(add-watch app-state :lg-watcher
           (fn [key a old-state new-state]
             (js/console.log "State changed! " (pr-str (keys (:scene old-state))) " -> " (pr-str (keys (:scene new-state))))))

(defn main* []
  (om/root
   (fn [app owner]
     (reify
       om/IRender
       (render [_]
         (let [current-lesson (:current-lesson app)
               summary        (nth lessons (:index current-lesson))]
           (dom/div #js{:className "row"}
                    (dom/div #js{:className "col-xs-0 col-md-2 col-lg-2 col-xl-2"}
                             (dom/h2 nil "Lessons")
                             (apply dom/ul nil
                                    (map (fn [idx lesson-summary]
                                           (dom/li #js{:style #js{:overflow "hidden"}}
                                                   (dom/a #js{:href "#"
                                                              :onClick (fn [event]
                                                                         ;; TODO: Call the exit fn of the old lesson and the entry fn of the new
                                                                         (om/transact! app [:current-lesson]
                                                                                       (fn [old-lesson]
                                                                                         (when-let [exit (get-in lessons [(:index old-lesson) :exit])]
                                                                                           (js/console.log "Exiting old lesson...")
                                                                                           (exit app  (get-in app [:live :gl])))
                                                                                         (let [new-lesson  (get-in lessons [idx])
                                                                                               next-lesson (merge new-lesson
                                                                                                                  {:index idx})]
                                                                                           (js/console.log "Entering new lesson...: " (pr-str next-lesson))
                                                                                           ((:enter new-lesson) app-state (get-in app [:live :node]))
                                                                                           next-lesson)))
                                                                         (.preventDefault event)
                                                                         (.stopPropagation event))} (:title lesson-summary)))) (range) lessons)))
                    (dom/div #js{:className "col-xs-12 col-md-10 col-lg-10 col-lg-12"}
                             (dom/div nil
                                      (dom/h2 #js{} (:title summary))
                                      (let [debug-data  (reduce merge {} (map (juxt identity #(get-in app %)) (:debug-keys summary)))]
                                        (when (seq debug-data)
                                          (dom/code nil (with-out-str (fipp/pprint debug-data {:width 80})))))
                                      (dom/div #js{:dangerouslySetInnerHTML #js{:__html (md/md->html (or (:explanation summary)
                                                                                                         "No explanation for this lesson"))}}))))))))
   app-state
   {:target (. js/document (getElementById "app"))}))

(defn main []
  (let [gl-node (js/document.getElementById "gl-canvas")
        gl      (.getContext gl-node "webgl")]
    (swap! app-state (fn [state]
                       (-> state
                           (update-in [:current-lesson] merge (nth lessons (get-in state [:current-lesson :index])))
                           (update-in [:live] merge {:node gl-node
                                                     :gl   gl}))))
    ((get-in @app-state [:current-lesson :enter]) app-state gl-node))
  (js/console.log (clj->js @app-state))
  (js/console.log "installed app")
  (main*))

(aset js/window "reinstallApp"
      (fn []
        (main*)))
