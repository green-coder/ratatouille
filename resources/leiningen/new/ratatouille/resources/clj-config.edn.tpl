{
{{#tag.http-kit}}
 :{{project.ns.name}}.ig/ring-handler {:message "Hello from the Ring handler."}
 :{{project.ns.name}}.ig/http-kit {:handler #ig/ref :{{project.ns.name}}.ig/ring-handler, :port 3000}}
{{/tag.http-kit}}
 }
