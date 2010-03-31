package org.lamsfoundation.lams.author.util
{
	import flash.display.Bitmap;
	import flash.display.BitmapData;
	import flash.display.DisplayObject;
	import flash.geom.Matrix;
	import flash.geom.Point;
	
	import mx.controls.Image;
	
	import org.lamsfoundation.lams.author.model.activity.Activity;
	import org.lamsfoundation.lams.author.model.activity.ToolActivity;
	
	public class AuthorUtil
	{
		public function AuthorUtil(){}
		
		/**
		 * Gets the midpoint between the two points
		 * (x1 + x2)/2 , (y1 + y2)/2
		 * 
		 * @param point1
		 * @param point2
		 * @return 
		 * 
		 */
		public static function getMidpoint(point1:Point, point2:Point):Point {
			var x:Number = (point1.x + point2.x) / 2;
			var y:Number = (point1.y + point2.y) / 2;
			return new Point(x,y);
		}
		
		
		public static function getBitmapData(target:DisplayObject):BitmapData
		{
			var bd:BitmapData = new BitmapData( target.width, target.height );
			var m:Matrix = new Matrix();
			bd.draw( target, m );
			return bd;
	   	}
	   	
	   	public static function getImage(target:DisplayObject):Image
		{
			var image:Image = new Image();
	        image.source = new Bitmap(getBitmapData(target));
			return image;
	   	}
	   	
	   	public static function activitySupportsGrouping(activity:Activity):Boolean {
	   		var activityTypeID:int = activity.activityTypeID;
	   		if (activityTypeID != Constants.ACTIVITY_TYPE_GATE_CONDITION &&
	   			activityTypeID != Constants.ACTIVITY_TYPE_GATE_PERMISSION &&
	   			activityTypeID != Constants.ACTIVITY_TYPE_GATE_SCHEDULE &&
	   			activityTypeID != Constants.ACTIVITY_TYPE_GATE_SYNCH &&
	   			activityTypeID != Constants.ACTIVITY_TYPE_GATE_SYSTEM &&
	   			activityTypeID != Constants.ACTIVITY_TYPE_GROUPING &&
	   			activityTypeID != Constants.ACTIVITY_TYPE_REFERENCE) {
	   			if (activity is ToolActivity) {
	   				return (activity as ToolActivity).groupingEnabled;
	   			} else {
	   				return true;
	   			}
	   		} else {
	   			return false;
	   		}
	   	}
	   		
	   		
	   		
	   		
	   		
	   		
	   	

	}
}