(defproject {{project.name}} "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.8.1"

{% if main.clj.ns %}  :main ^:skip-aot {{main.clj.ns.name}}

{% endif %}  :source-paths {{project.source-paths|safe}}
  :resource-paths ["resources" "target"]
  :target-path "target/%s"

{% if project.dependencies|not-empty %}  :dependencies {{project.dependencies|multi-line:16|safe}}

{% endif %}{% if project.plugins|not-empty %}  :plugins {{project.plugins|multi-line:11|safe}}

{% endif %}{% if project.aliases|not-empty %}  :aliases {{project.aliases|multi-line:11|safe}}

{% endif %}{% if project.profiles|not-empty %}  :profiles {{project.profiles|multi-line:12|safe}}{% endif %})
