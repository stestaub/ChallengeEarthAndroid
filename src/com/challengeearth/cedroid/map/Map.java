package com.challengeearth.cedroid.map;

import java.util.List;

import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.util.CloudmadeUtil;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapView.LayoutParams;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.MyLocationOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.TilesOverlay;

import android.app.Activity;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.challengeearth.cedroid.ChallengeData;
import com.challengeearth.cedroid.DetailsActivity;
import com.challengeearth.cedroid.R;
import com.challengeearth.cedroid.helpers.ResourceProxyImpl;

public class Map {

	private ViewGroup mapContainer;
	private MapView mOsmv;
	private TilesOverlay mTilesOverlay;
	private MapTileProviderBasic mProvider;
	private ResourceProxyImpl mResourceProxy;
	private MyLocationOverlay mLocationOverlay;
	private ItemizedOverlay<OverlayItem> chalLocOverlay;
	
	private Activity context;
	
	/**
	 * The item gesture listener for the map
	 */
	final OnItemGestureListener<OverlayItem> pOnItemGestureListener = new OnItemGestureListener<OverlayItem>() {

		@Override
		public boolean onItemLongPress(int index, OverlayItem item) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onItemSingleTapUp(int index, OverlayItem item) {
			long c_id = Long.parseLong(item.getTitle());
			Intent intent = new Intent(Map.this.context, DetailsActivity.class);
			intent.putExtra(ChallengeData.C_ID, c_id);
			Map.this.context.startActivity(intent);
			return true;
		}
	};
	
	
	public Map(Activity context, ViewGroup mapContainer) {
		CloudmadeUtil.retrieveCloudmadeKey(context);
	    
		this.context = context;
		
	    this.mResourceProxy = new ResourceProxyImpl(context);
	    this.mapContainer = mapContainer;
		this.mOsmv = new MapView(context, 256, mResourceProxy);
		
		this.mOsmv.setBuiltInZoomControls(true);
		this.mOsmv.setMultiTouchControls(true);

		// Add tiles layer
		mProvider = new MapTileProviderBasic(context);
		mProvider.setTileSource(TileSourceFactory.CLOUDMADESTANDARDTILES);
		this.mTilesOverlay = new TilesOverlay(mProvider, context);
		this.mOsmv.getOverlays().add(this.mTilesOverlay);
		
		// zoom to switzerland
		this.mOsmv.getController().setZoom(7);
		
		this.mLocationOverlay = new MyLocationOverlay(context, this.mOsmv,
				mResourceProxy);
		this.mOsmv.getOverlays().add(mLocationOverlay);
		this.mLocationOverlay.enableMyLocation();
		this.mLocationOverlay.enableFollowLocation();
		this.mLocationOverlay.setDrawAccuracyEnabled(true);
	    
		this.mapContainer.addView(this.mOsmv, new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
	}
	
	public void addChallengeOverlays(List<OverlayItem> chalLocOverlayArray) {
		this.mOsmv.getOverlays().remove(this.chalLocOverlay);
		this.chalLocOverlay = new ItemizedIconOverlay<OverlayItem>(chalLocOverlayArray, 
				this.context.getResources().getDrawable(R.drawable.pin_map), 
				pOnItemGestureListener, 
				mResourceProxy);
		this.mOsmv.getOverlays().add(chalLocOverlay);
	}
	
	public void destroy() {
		this.mLocationOverlay.disableMyLocation();
		this.mLocationOverlay = null;
		this.chalLocOverlay = null;
		this.mOsmv.destroyDrawingCache();
		this.mOsmv = null;
		this.mTilesOverlay = null;
	}
	
	public void setZoomVisible(boolean visible) {
		this.mOsmv.setBuiltInZoomControls(visible);
	}
}
