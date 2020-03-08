(ns thingy-editor-proto.metro-parks-tacoma.core
    (:require
     [thingy.core :as t]
     [thingy.backend.dom :as dom-backend]
     [thingy.backend.http :as http-backend]))


(def media-library [{:uri "/metroparkstacoma/media-library/fort-hero-2-345x390.jpg"
                     :alt "Fort Hero"}
                    {:uri "/metroparkstacoma/media-library/Marina_8LaneBoatLaunch_FacingBreakwater-345x390.jpg"
                     :alt "Boat Launcher"}
                    {:uri "/metroparkstacoma/media-library/hero-baja-bay-girl-boy-shark-1440x500-crop-1578494576-861x500.jpg"
                     :alt "Baja Bay"}
                    {:uri "/metroparkstacoma/media-library/nwtrek-715x415.jpg"
                     :alt "NW Trek"}])



(defn init! []
  (t/editable! js/document.body
               {:editables [{:selector ".tabbed-slideshow"
                             :contenteditable false
                             :tools [{:name :image
                                      :control ".slick-current img"
                                      :library media-library}]}]
                :backends [dom-backend/backend
                           http-backend/backend]
                :http {:endpoint "/wp-json"}}))