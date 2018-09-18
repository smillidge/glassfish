/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

Fx.Scroll = Class.create();
Fx.Scroll.prototype = Object.extend(new Fx.Base(), {

	initialize: function(el, options) {
		this.element = $(el);
		this.setOptions(options);
		this.element.style.overflow = 'hidden';
	},
	
	down: function(){
		return this.custom(this.element.scrollTop, this.element.scrollHeight-this.element.offsetHeight);
	},
	
	up: function(){
		return this.custom(this.element.scrollTop, 0);
	},

	increase: function(){
		this.element.scrollTop = this.now;
	}

});

//fx.Color, originally by Tom Jensen (http://neuemusic.com) MIT-style LICENSE.

Fx.Color = Class.create();
Fx.Color.prototype = Object.extend(new Fx.Base(), {
	
	initialize: function(el, property, options){
		this.element = $(el);
		this.setOptions(options);
		this.property = property.camelize();
		this.now = [];
	},

	custom: function(from, to){
		return this._start(from.hexToRgb(true), to.hexToRgb(true));
	},

	setNow: function(){
		[0,1,2].each(function(i){
			this.now[i] = Math.round(this.compute(this.from[i], this.to[i]));
		}.bind(this));
	},

	increase: function(){
		this.element.style[this.property] = "rgb("+this.now[0]+","+this.now[1]+","+this.now[2]+")";
	}

});

Object.extend(String.prototype, {

	rgbToHex: function(array){
		var rgb = this.match(new RegExp('([\\d]{1,3})', 'g'));
		if (rgb[3] == 0) return 'transparent';
		var hex = [];
		for (var i = 0; i < 3; i++){
			var bit = (rgb[i]-0).toString(16);
			hex.push(bit.length == 1 ? '0'+bit : bit);
		}
		var hexText = '#'+hex.join('');
		if (array) return hex;
		else return hexText;
	},

	hexToRgb: function(array){
		var hex = this.match(new RegExp('^[#]{0,1}([\\w]{1,2})([\\w]{1,2})([\\w]{1,2})$'));
		var rgb = [];
		for (var i = 1; i < hex.length; i++){
			if (hex[i].length == 1) hex[i] += hex[i];
			rgb.push(parseInt(hex[i], 16));
		}
		var rgbText = 'rgb('+rgb.join(',')+')';
		if (array) return rgb;
		else return rgbText;
	}	

});
