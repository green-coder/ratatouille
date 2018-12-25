(defproject {{project.name}} "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.8.1"

  :main ^:skip-aot {{core.ns.name}}

  :source-paths {{{project.source-paths}}}
  :resource-paths ["resources" "target"]
  :target-path "target/%s"

  :dependencies {{{project.dependencies}}}

  :aliases {{{project.aliases}}}

  :profiles {:dev {:dependencies {{{project.profiles.dev.dependencies}}}}
             :uberjar {:aot :all}})
