grails-cacheable-rest
=====================

The Grails CacheableRest plugin adds cache-header functionality provided by the [Caching headers Plugin](http://grails.org/plugin/cache-headers) to Grails REST resource controllers.

A new base controller class [CacheableRestfulController](https://github.com/ajbrown/grails-cacheable-rest/blob/master/src/groovy/grails/plugin/restfulCacheHeaders/CacheableRestfulController.groovy) is provided, which extends the grails provided RestfulController to add ETag support.
