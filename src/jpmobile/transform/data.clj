;;
;;Copyright 2015 Patrick Boe
;;
;;This file is part of jpmobile.
;;
;;jpmobile is free software: you can redistribute it and/or modify
;;it under the terms of the GNU General Public License as published by
;;the Free Software Foundation, either version 3 of the License, or
;;(at your option) any later version.
;;
;;jpmobile is distributed in the hope that it will be useful,
;;but WITHOUT ANY WARRANTY; without even the implied warranty of
;;MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;;GNU General Public License for more details.
;;
;;You should have received a copy of the GNU General Public License
;;along with jpmobile.  If not, see <http://www.gnu.org/licenses/>.
;;
(ns jpmobile.transform.data
  (:require [jpmobile.transform.coding :as coding]))

(defn site-data [js]
  { :notes (coding/code-table js),
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
         [ "contributenotes" "Contribute Notes" ]
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
