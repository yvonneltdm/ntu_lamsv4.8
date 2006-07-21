﻿/***************************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * License Information: http://lamsfoundation.org/licensing/lams/2.0/
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2.0 
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ************************************************************************
 */

import org.lamsfoundation.lams.common.util.*
import org.lamsfoundation.lams.common.ui.*
import org.lamsfoundation.lams.common.style.*
import org.lamsfoundation.lams.monitoring.mv.*
import org.lamsfoundation.lams.monitoring.mv.tabviews.*
import org.lamsfoundation.lams.monitoring.*;
import org.lamsfoundation.lams.common.dict.*
import org.lamsfoundation.lams.common.mvc.*
import org.lamsfoundation.lams.common.ToolTip;
import mx.managers.*
import mx.containers.*
import mx.events.*
import mx.utils.*
import mx.controls.*


/**
*Monitoring view for the Monitor
* Relects changes in the MonitorModel
*/

class org.lamsfoundation.lams.monitoring.mv.MonitorView extends AbstractView{
	
	private var _className = "MonitorView";
	
	//constants:
	private var GRID_HEIGHT:Number;
	private var GRID_WIDTH:Number;
	private var H_GAP:Number;
	private var V_GAP:Number;
	private var Offset_Y_TabLayer_mc:Number;
	private var _tm:ThemeManager;
	private var _tip:ToolTip;
	
	private var _monitorView_mc:MovieClip;
	
	//Canvas clip
	private var _monitor_mc:MovieClip;
	private var monitor_scp:MovieClip;
	private var monitorTabs_tb:MovieClip;
	private var learnerMenuBar:MovieClip;
    private var bkg_pnl:MovieClip;
	
    private var _gridLayer_mc:MovieClip;
    private var _lessonTabLayer_mc:MovieClip;
	private var _monitorTabLayer_mc:MovieClip;
	private var _learnerTabLayer_mc:MovieClip;
	private var _todoTabLayer_mc:MovieClip;
	private var refresh_btn:Button;
	private var help_btn:Button;
	private var exportPortfolio_btn:Button;
	//private var _activityLayerComplex_mc:MovieClip;
	//private var _activityLayer_mc:MovieClip;
	
	//private var _transitionPropertiesOK:Function;
    private var _monitorView:MonitorView;
	private var _monitorModel:MonitorModel;
	//Tab Views Initialisers
	
	//LessonTabView
	private var lessonTabView:LessonTabView;
	private var lessonTabView_mc:MovieClip;
	//MonitorTabView
	private var monitorTabView:MonitorTabView;
	private var monitorTabView_mc:MovieClip;
	//TodoTabView
	private var todoTabView:TodoTabView;
	private var todoTabView_mc:MovieClip;
	//LearnerTabView
	private var learnerTabView:LearnerTabView;
	private var learnerTabView_mc:MovieClip;
	
	private var _monitorController:MonitorController;
	
    //Defined so compiler can 'see' events added at runtime by EventDispatcher
    private var dispatchEvent:Function;     
    public var addEventListener:Function;
    public var removeEventListener:Function;
	
	
	/**
	* Constructor
	*/
	function MonitorView(){
		_monitorView = this;
		_tm = ThemeManager.getInstance();
		_tip = new ToolTip();
		//Init for event delegation
        mx.events.EventDispatcher.initialize(this);
	}
	
	/**
	* Called to initialise Canvas  . CAlled by the Canvas container
	*/
	public function init(m:Observable,c:Controller,x:Number,y:Number,w:Number,h:Number){

		super (m, c);
        //Set up parameters for the grid
		H_GAP = 10;
		V_GAP = 10;
		//_monitorModel = getModel();
		MovieClipUtils.doLater(Proxy.create(this,draw)); 
		
    }    
	
	private function tabLoaded(evt:Object){
        Debugger.log('viewLoaded called',Debugger.GEN,'tabLoaded','MonitorView');
		
		if(evt.type=='load') {
            //dispatchEvent({type:'load',target:this});
        }else {
            //Raise error for unrecognized event
        }
    }
	
	/**
	 * Recieved update events from the CanvasModel. Dispatches to relevent handler depending on update.Type
	 * @usage   
	 * @param   event
	 */
	public function update (o:Observable,infoObj:Object):Void{
		
		var mm:MonitorModel = MonitorModel(o);
		_monitorController = getController();

		switch (infoObj.updateType){
			case 'POSITION' :
				setPosition(mm);
                break;
            case 'SIZE' :
			    setSize(mm);
                break;
			case 'TABCHANGE' :
				showData(mm);
				break;
			case 'EXPORTSHOWHIDE' :
				exportShowHide(infoObj.data);
				break;
            default :
                Debugger.log('unknown update type :' + infoObj.updateType,Debugger.CRITICAL,'update','org.lamsfoundation.lams.MonitorView');
		}

	}
	
	/**
    * Sets the size of the canvas on stage, called from update
    */
	private function showData(mm:MonitorModel):Void{
        var s:Object = mm.getSequence();
		trace("Item Description is : "+s._learningDesignID);
		
	}
	
	private function exportShowHide(v:Boolean):Void{
		exportPortfolio_btn.visible = v;
	}
	
	/**
    * layout visual elements on the canvas on initialisation
    */
	private function draw(){
		trace("Height of learnerMenuBar: "+learnerMenuBar._height)
		var mcontroller = getController();
		
		//get the content path for Tabs
		_monitor_mc = monitor_scp.content;
		
		_lessonTabLayer_mc = _monitor_mc.createEmptyMovieClip("_lessonTabLayer_mc", _monitor_mc.getNextHighestDepth());
		
		
		_monitorTabLayer_mc = _monitor_mc.createEmptyMovieClip("_monitorTabLayer_mc", _monitor_mc.getNextHighestDepth());
		
		_todoTabLayer_mc = _monitor_mc.createEmptyMovieClip("_todoTabLayer_mc", _monitor_mc.getNextHighestDepth());
		
		_learnerTabLayer_mc = _monitor_mc.createEmptyMovieClip("_learnerTabLayer_mc", _monitor_mc.getNextHighestDepth());
		
		var tab_arr:Array = [{label:Dictionary.getValue('mtab_lesson'), data:"lesson"}, {label:Dictionary.getValue('mtab_seq'), data:"monitor"}, {label:Dictionary.getValue('mtab_learners'), data:"learners"}];
		
		monitorTabs_tb.dataProvider = tab_arr;
		monitorTabs_tb.selectedIndex = 0;
		
		refresh_btn.addEventListener("click",mcontroller);
		help_btn.addEventListener("click",mcontroller);
		exportPortfolio_btn.addEventListener("click", mcontroller);
		
		refresh_btn.onRollOver = Proxy.create(this,this['showToolTip'], refresh_btn, "refresh_btn_tooltip");
		refresh_btn.onRollOut = Proxy.create(this,this['hideToolTip']);
		
		help_btn.onRollOver = Proxy.create(this,this['showToolTip'], help_btn, "help_btn_tooltip");
		help_btn.onRollOut = Proxy.create(this,this['hideToolTip']);
		
		exportPortfolio_btn.onRollOver = Proxy.create(this,this['showToolTip'], exportPortfolio_btn, "class_exportPortfolio_btn_tooltip");
		exportPortfolio_btn.onRollOut = Proxy.create(this,this['hideToolTip']);
		
		monitorTabs_tb.addEventListener("change",mcontroller);
		
		setLabels();
		setStyles();
		setupTabInit()
	    dispatchEvent({type:'load',target:this});
		
	}
	
	private function setupTabInit(){
		
		
		
		var mm:Observable = getModel();
		// Inititialsation for Lesson Tab View 
		lessonTabView_mc = _lessonTabLayer_mc.attachMovie("LessonTabView", "lessonTabView_mc",DepthManager.kTop)
		lessonTabView_mc._visible = false;
		lessonTabView = LessonTabView(lessonTabView_mc);
		lessonTabView.init(mm, undefined);
		lessonTabView.addEventListener('load',Proxy.create(this,tabLoaded));
			
		// Inititialsation for Monitor Tab View 
		monitorTabView_mc = _monitorTabLayer_mc.attachMovie("MonitorTabView", "monitorTabView_mc",DepthManager.kTop)
		monitorTabView_mc._visible = false;
		monitorTabView = MonitorTabView(monitorTabView_mc);
		monitorTabView.init(mm, undefined);
		monitorTabView.addEventListener('load',Proxy.create(this,tabLoaded));
		
		// Inititialsation for Learner Tab View 
		learnerTabView_mc = _learnerTabLayer_mc.attachMovie("LearnerTabView", "learnerTabView_mc",DepthManager.kTop)
		learnerTabView_mc._visible = false;
		learnerTabView = LearnerTabView(learnerTabView_mc);
		learnerTabView.init(mm, undefined);
		learnerTabView.addEventListener('load',Proxy.create(this,tabLoaded));
		
		// Inititialsation for Todo Tab View 
		/*todoTabView_mc = _todoTabLayer_mc.attachMovie("TodoTabView", "todoTabView_mc",DepthManager.kTop)
		todoTabView_mc._visible = false;
		todoTabView = TodoTabView(todoTabView_mc);
		todoTabView.init(mm, undefined);
		todoTabView.addEventListener('load',Proxy.create(this,tabLoaded));
		
		//Observers for All the Tab Views
		mm.addObserver(todoTabView);
		*/
		mm.addObserver(lessonTabView);
		mm.addObserver(monitorTabView);
		mm.addObserver(learnerTabView);
		
		
		
	}
	
	public function showToolTip(btnObj, btnTT:String):Void{
		var btnLabel = btnObj.label;
		var xpos:Number;
		if (btnLabel == "Help"){
			xpos = btnObj._x - 105
		}else if (btnLabel == "Refresh"){
			xpos = btnObj._x - 40
		}else{
			xpos = btnObj._x
		}
		var Xpos = Application.MONITOR_X+ xpos;
		var Ypos = (Application.MONITOR_Y+ btnObj._y+btnObj.height)+5;
		var ttHolder = Application.tooltip;
		var ttMessage = Dictionary.getValue(btnTT);
		_tip.DisplayToolTip(ttHolder, ttMessage, Xpos, Ypos);
		
	}
	
	public function hideToolTip():Void{
		_tip.CloseToolTip();
	}

	/**
	 * Get the CSSStyleDeclaration objects for each component and apply them
	 * directly to the instance  
	 */
	private function setStyles():Void{
		var styleObj = _tm.getStyleObject('BGPanel');
		bkg_pnl.setStyle('styleName',styleObj);
		styleObj = _tm.getStyleObject('scrollpane');
		monitor_scp.setStyle('styleName',styleObj);
		styleObj = _tm.getStyleObject('button');
		monitorTabs_tb.setStyle('styleName', styleObj);
		refresh_btn.setStyle('styleName',styleObj);
		exportPortfolio_btn.setStyle('styleName',styleObj);
		help_btn.setStyle('styleName',styleObj);
		
	}
	
	private function setLabels():Void{
		refresh_btn.label = Dictionary.getValue('refresh_btn');
		help_btn.label = Dictionary.getValue('help_btn');
		exportPortfolio_btn.label = Dictionary.getValue('learner_exportPortfolio_btn');
	}
		
	/**
    * Sets the size of the canvas on stage, called from update
    */
	private function setSize(mm:MonitorModel):Void{
        var s:Object = mm.getSize();
		trace("Monitor Tab Widtht: "+s.w+" Monitor Tab Height: "+s.h);
		bkg_pnl.setSize(s.w,s.h);
		trace("Monitor View Stage Width "+s.w+" and Monitor View Stage height "+s.h)
		trace("Monitor View bg panel Width "+bkg_pnl.width+" and Monitor View bg panel height "+bkg_pnl.height)
		monitor_scp.setSize(s.w-monitor_scp._x,s.h-monitor_scp._y);
		exportPortfolio_btn._x = s.w - 260;
		refresh_btn._x = s.w - 160
		help_btn._x = s.w - 80
				
	}
	
	 /**
    * Sets the position of the canvas on stage, called from update
    * @param cm Canvas model object 
    */
	private function setPosition(mm:MonitorModel):Void{
        var p:Object = mm.getPosition();
		trace("X pos set in Model is: "+p.x+" and Y pos set in Model is "+p.y)
        this._x = p.x;
        this._y = p.y;
	}
	
	public function getLessonTabView():LessonTabView{
		return lessonTabView;
	}
	
	/**
	 * Overrides method in abstract view to ensure cortect type of controller is returned
	 * @usage   
	 * @return  CanvasController
	 */
	public function getController():MonitorController{
		var c:Controller = super.getController();
		return MonitorController(c);
	}
	
	public function getMonitorTab():MovieClip{
		return monitorTabs_tb;
	}
	
	public function getMonitorScp():MovieClip{
		trace("Called getMonitorScp")
		return monitor_scp;
	}
	
	/*
    * Returns the default controller for this view.
    */
    public function defaultController (model:Observable):Controller {
        return new MonitorController(model);
    }
	
}