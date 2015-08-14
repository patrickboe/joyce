(ns codes)

;;plan:

;;replace rgb(64, 179, 36) with ireland
;;replace rgb(245, 150, 39) with literature
;;replace rgb(156, 99, 42) with dublin
;;replace rgb(171, 89, 194) with performances
;;replace rgb(207, 41, 41) with bodily
;;replace rgb(48, 126, 227) with artist

(defn always-notes [_] "note")

(def site-data
  { :notes always-notes,
   :pages
   [
     [ "Notes"
       [
         [ "colorcoding" "Color Coding" ]
         [ "aboutnotes" "About the Notes" ]
         [ "tally" "Tally of Notes" ]
       ] ]
     [ "The Site" 
       [
         [ "aboutproject" "About the Project" ]
         [ "contributors" "Contributors" ]
         [ "sources" "Sources" ]
         [ "fairuse" "Fair Use" ]
         [ "latestnews" "Latest News" ]
       ] ]
     [ "Resources"
       [
         [ "people" "People in the Novel" ]
         [ "times" "Times in the Novel" ]
       ] ]
     [ "Editions"
       [
         [ "ourtext" "Text and Pagination" ]
       ] ]
     [ "Search"
       [
         [ "searchchapters" "Search Chapters" ]
         [ "searchnovel" "Search Entire Novel" ]
       ] ]
     [ "Contact Us"
       [
         [ "corrections" "Corrections" ]
         [ "contributors" "Contribute Notes" ]
       ] ]
    ]
   :chapters
   [
    [ "telem" "Telemachus" ]
    [ "nestor" "Nestor" ]
    [ "proteus" "Proteus" ]
    [ "calypso" "Calypso" ]
    [ "lotus" "Lotus Eaters" ]
    [ "hades" "Hades" ]
    [ "aeolus" "Aeolus" ]
    [ "lestry" "Lestrygonians" ]
    [ "scylla" "Scylla and Charybdis" ]
    [ "wrocks" "Wandering Rocks" ]
    [ "sirens" "Sirens" ]
    [ "cyclops" "Cyclops" ]
    [ "nausicaa" "Nausicaa" ]
    [ "oxen" "Oxen of the Sun" ]
    [ "circe" "Circe" ]
    [ "eumaeus" "Eumaeus" ]
    [ "ithaca" "Ithaca" ]
    [ "penelope" "Penelope" ]]})
