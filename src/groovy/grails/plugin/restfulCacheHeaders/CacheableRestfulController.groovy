package grails.plugin.restfulCacheHeaders

import grails.artefact.Artefact

/**
 * A base class for REST resources, with additional methods available for responding with cache-friendly ETag and
 * LastModified headers based on the "lastUpdated" and "hashCode" of the objects being sent in the response.  By
 * default, the "index", and "show" methods will send cache-friendly headers.
 *
 * TODO allow specifying custom etag generators for collections and items.
 */
@Artefact("Controller")
abstract class CacheableRestfulController<T> extends grails.rest.RestfulController<T> {
    static responseFormats = ['json', 'hal']

    CacheableRestfulController( Class<T> resource, boolean readOnly = false ) {
        super( resource, readOnly )
    }

    @Override
    def index( Integer max ) {
        params.max = Math.min(max ?: 10, 100)
        respondWithCacheHeaders listAllResources(params), [("${resourceName}Count".toString()): countResources()]
    }

    @Override
    def show() {
        respondWithCacheHeaders queryForResource( params.id )
    }

    /**
     * Send the specified items in the response with cache headers (ETag only).
     * @param items
     * @param args additional arguments to send to the respond method, such as a status.
     * @return
     */
    protected respondWithCacheHeaders( Collection items, args = [:] ) {
        withCacheHeaders {
            etag {
                "${params.id}:${items.collect{ it.hashCode().toString() }.join(':')}".encodeAsSHA256()
            }

            generate {
                respond items, args
            }
        }
    }

    /**
     * Send the specified resource instance with cache headers (ETag and Last-Modified)
     * @param domain a grails domain class instance.
     * @param args additional args to pass for the respond method call.
     * @return
     */
    protected respondWithCacheHeaders( T domain, args = [:] ) {
        withCacheHeaders {

            etag {
                //We need to use the hashCode here.  Previously we were using the object's ID and version.  The
                // problem with that approach is that if a field is added to a domain object and it never gets
                // persisted, clients would never get the updated representation.  Since the hashcode includes nearly
                // all of the properties, updates to the objects themselves will result in new ETags

                domain?.hashCode()?.encodeAsSHA256()
            }

            if( domain?.hasProperty( 'lastUpdated' ) ) {
                delegate.lastModified { domain.lastUpdated }
            }

            generate {
                respond domain, args
            }
        }
    }
}
