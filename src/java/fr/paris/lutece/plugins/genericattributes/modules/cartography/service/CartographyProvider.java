/*
 * Copyright (c) 2002-2017, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.genericattributes.modules.cartography.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.carto.business.DataLayer;
import fr.paris.lutece.plugins.carto.business.DataLayerHome;
import fr.paris.lutece.plugins.carto.business.MapTemplateHome;
import fr.paris.lutece.plugins.genericattributes.business.ICartoProvider;
import fr.paris.lutece.plugins.leaflet.business.GeolocItem;
import fr.paris.lutece.plugins.leaflet.business.GeolocItemPolygon;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;


/**
 * 
 * OpenStreetMapProvider : provides Open street map support for Generic Attributes
 * 
 */
public class CartographyProvider implements ICartoProvider
{
    private static final String PROPERTY_KEY = "genericattributes-openstreetmap.key";
    private static final String PROPERTY_DISPLAYED_NAME = "genericattributes-openstreetmap.displayName";
    private static final String TEMPLATE_HTML = "/admin/plugins/genericattributes/modules/openstreetmap/OpenStreetMapTemplate.html";
    private static final String TEMPLATE_RECAP_HTML = "/admin/plugins/genericattributes/modules/openstreetmap/OpenStreetMapTemplateRecap.html";

    /**
     * {@inheritDoc}
     */
    public String getKey( )
    {
        return AppPropertiesService.getProperty( PROPERTY_KEY );
    }

    /**
     * {@inheritDoc}
     */
    public String getDisplayedName( )
    {
        return AppPropertiesService.getProperty( PROPERTY_DISPLAYED_NAME );
    }

    /**
     * {@inheritDoc}
     */
    public String getHtmlCode( )
    {
        return TEMPLATE_HTML;
    }
    /**
     * {@inheritDoc}
     */
    public String getHtmlRecapCode( )
    {
        return TEMPLATE_RECAP_HTML;
    }

    /**
     * {@inheritDoc}
     */
    public ReferenceItem toRefItem( )
    {
        ReferenceItem refItem = new ReferenceItem( );

        refItem.setCode( getKey( ) );
        refItem.setName( getDisplayedName( ) );

        return refItem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString( )
    {
        return "Cartography Provider";
    }

	@Override
	public Object getParameter(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
     * Builds the {@link ReferenceList} of all available map providers
     * 
     * @return the {@link ReferenceList}
     */
	@Override
    public ReferenceList getMapProvidersRefList( )
    {
    	return MapTemplateHome.getMapTemplatesReferenceList( );
    }
	
	/**
     * {@inheritDoc}
     */
	@Override
	public String getGeolocItemPoint( Double x, Double y, String adresse )
    {	
	        
	        GeolocItem geolocItem = new GeolocItem( );
	        HashMap<String, Object> properties = new HashMap<>( );
	        properties.put( GeolocItem.PATH_PROPERTIES_ADDRESS, adresse );
	
	        HashMap<String, Object> geometry = new HashMap<>( );
	        geometry.put( GeolocItem.PATH_GEOMETRY_COORDINATES, Arrays.asList( x, y ) );
	        geometry.put( GeolocItem.PATH_GEOMETRY_TYPE, GeolocItem.VALUE_GEOMETRY_TYPE );
	        geolocItem.setGeometry( geometry );
	        geolocItem.setProperties( properties );
	        
	        return geolocItem.toJSON();
    }
    
	/**
     * {@inheritDoc}
     */
	@Override
    public String getGeolocItemPolygon( String coordinate )
    {
		GeolocItemPolygon geoPolygon = getGeolocItemGeneral( coordinate, GeolocItem.VALUE_GEOMETRY_TYPE_POLYGON );
        
        geoPolygon.setTypegeometry( GeolocItem.VALUE_GEOMETRY_TYPE_POLYGON );
        
        return geoPolygon.toJSON();
    }
	
	/**
     * {@inheritDoc}
     */
	@Override
    public String getGeolocItemPolyline( String coordinate )
    {
		GeolocItemPolygon geoPolygon = getGeolocItemGeneral( coordinate, GeolocItem.VALUE_GEOMETRY_TYPE_POLYLINE );
        
        geoPolygon.setTypegeometry( GeolocItem.VALUE_GEOMETRY_TYPE_POLYLINE );
        
        return geoPolygon.toJSON();
    }

	@Override
	public String getSolrTag(String strIdLayer) {
		Optional<DataLayer> dataLayer = DataLayerHome.findByPrimaryKey( Integer.valueOf( strIdLayer ) );
		if ( dataLayer.isPresent( ) )
		{
			return dataLayer.get( ).getSolrTag( );
		}
		else 
		{
			return StringUtils.EMPTY;
		}
		
	}
	
	public GeolocItemPolygon getGeolocItemGeneral( String coordinate, String strTypeGeometry )
	{
        GeolocItemPolygon geoPolygon = new GeolocItemPolygon();
        
        String[] lstCoordPolygon = coordinate.split(";");
        List<List<Double>> polygonLonLoat = new ArrayList<>( );
        List<List<List<Double>>> polygonCoord = new ArrayList<>( );
        HashMap<String, Object> geometryPolygon = new HashMap<>( );
        
        for (String coordPolygonXY : lstCoordPolygon )
        {
        	String [] coordPolygonXY2 = coordPolygonXY.split( "," );
        	double polygonx = Double.valueOf( coordPolygonXY2[0] );
            double polygony = Double.valueOf( coordPolygonXY2[1] );
            polygonLonLoat.add( Arrays.asList( polygonx, polygony ) );
        }
        
        if ( strTypeGeometry.equals( GeolocItemPolygon.VALUE_GEOMETRY_TYPE_POLYGON ) )
        {
	        String [] coordPolygonXY2 = lstCoordPolygon[0].split( "," );
	    	double polygonx = Double.valueOf( coordPolygonXY2[0] );
	        double polygony = Double.valueOf( coordPolygonXY2[1] );
	        polygonLonLoat.add( Arrays.asList( polygonx, polygony ) );
	        polygonCoord.add( polygonLonLoat );
	        geometryPolygon.put( GeolocItem.PATH_GEOMETRY_COORDINATES, polygonCoord );
        }
        else if ( strTypeGeometry.equals( GeolocItemPolygon.VALUE_GEOMETRY_TYPE_POLYLINE ) )
        {
        	geometryPolygon.put( GeolocItem.PATH_GEOMETRY_COORDINATES, polygonLonLoat );
        }
        
        geoPolygon.setGeometry( geometryPolygon );
        
        return geoPolygon;
	}

}
