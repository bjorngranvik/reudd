class UrlMappings {
    static mappings = {
      "/$controller/$action?/$id?"{
	      constraints {
			 // apply constraints here
		  }
	  }
      "/debug"(view:'/debug')
      "/"(controller:"user")
	  "500"(view:'/error')
	}
}
