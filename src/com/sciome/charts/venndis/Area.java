package com.sciome.charts.venndis;


// Copyright (C) 2014 Vladimir Ignatchenko (vladimirsign@gmail.com)
// Dr. Thomas Kislinger laboratory (http://kislingerlab.uhnres.utoronto.ca/)
//
// This file is part of VennDIS software.
// VennDIS is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// VennDIS is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with VennDIS. If not, see <http://www.gnu.org/licenses/>.

public class Area {

	public static double getCircleArea(double radius) {
		double circleArea = 3.14 * radius * radius;
		return circleArea;
	}

	public static double getSectorOfCircleArea(double radius, double angle) {
		double sectorOfCircleArea = (angle/360) * 3.14 * radius * radius;
		return sectorOfCircleArea;
	}

	public static double getCircleRadius(double area) {
		double circleRadius = Math.sqrt(area/3.14);
		return circleRadius;
	}

	public static double getTriangleArea(double sideA, double sideB, double sideC) {
		double halfPerimeter = (sideA + sideB + sideC)/2;
		double triangleArea = Math.sqrt( halfPerimeter * (halfPerimeter-sideA) * (halfPerimeter-sideB) * (halfPerimeter-sideC) );
		return triangleArea;
	}

	public static double getTriangleArea(double radius, double angle) {
		double triangleArea = radius*radius * Math.sin( Math.toRadians(Math.abs(angle)) )/2;
		return triangleArea;
	}

	public static double getTriangleHight(double radiusA, double radiusB, double distance) {
		double halfPerimeter = (radiusA + radiusB + distance)/2;
		double triangleArea = Math.sqrt( halfPerimeter * (halfPerimeter-radiusA) * (halfPerimeter-radiusB) * (halfPerimeter-distance) );
		double triangleHight = 2*(triangleArea/distance);
		return triangleHight;
	}

	public static double getTriangleAlphaAngle(double oppositeSide, double hypotenusa) {
		double alphaAngle = Math.toDegrees(Math.asin(oppositeSide/hypotenusa));
		return alphaAngle;
	}

	public static double getOverlapArea(double radiusA, double radiusB, double distance) {
		double triangleHight = getTriangleHight(radiusA, radiusB, distance);
		double d = Math.sqrt(radiusA*radiusA-triangleHight*triangleHight);
		double alphaAngleA = getTriangleAlphaAngle(triangleHight, radiusA);
		double alphaAngleB = getTriangleAlphaAngle(triangleHight, radiusB);
		double tA = getTriangleArea(radiusA, alphaAngleA*2);
		double tB = getTriangleArea(radiusB, alphaAngleB*2);
		double sA = getSectorOfCircleArea(radiusA, alphaAngleA*2);
		double ssA = sA - tA;
		double sB = 0;
		double ssB = 0;
		if (distance<d)	{
			sB = getSectorOfCircleArea(radiusB, 360-alphaAngleB*2);
			ssB = sB + tB;
		} else {
			sB = getSectorOfCircleArea(radiusB, alphaAngleB*2); 
			ssB = sB - tB;
		}
		double overlapArea = ssA+ssB;
		return overlapArea;
	}

	public static double getDistance(double valueA, double valueB, double valueAB) {
		double radiusA = 0;
		double radiusB = 0;
		double distanceAB = 0;
		double circleAreaA = 0;
		double circleAreaB = 0;
		double circleAreaAB = 0;
		double overlapArea = 0;
		if (valueA>valueB) {
			radiusA = 100;
			circleAreaA = getCircleArea(radiusA);
			circleAreaB = (valueB/valueA)*circleAreaA;
			circleAreaAB = (valueAB/valueA)*circleAreaA;
			radiusB = getCircleRadius(circleAreaB);
			if (Math.min(valueA, valueB)==valueAB) {
				distanceAB = Math.max(radiusA, radiusB) - Math.min(radiusA, radiusB);
			} else {
				for (int i=0; i<=(radiusB*2); i++) {
					overlapArea = getOverlapArea(radiusA, radiusB, radiusA+radiusB-i);
					distanceAB = radiusA+radiusB-i;
					if (overlapArea > circleAreaAB) {  break; }
				}
			}
		} else if (valueA<valueB) {
			radiusB = 100;
			circleAreaB = getCircleArea(radiusB);
			circleAreaA = (valueA/valueB)*circleAreaB;
			circleAreaAB = (valueAB/valueB)*circleAreaB;
			radiusA = getCircleRadius(circleAreaA);
			if (Math.min(valueA, valueB)==valueAB) {
				distanceAB = Math.max(radiusA, radiusB) - Math.min(radiusA, radiusB);
			} else {
				for (int i=0; i<=(radiusA*2); i++) {
					overlapArea = getOverlapArea(radiusB, radiusA, radiusB+radiusA-i);
					if (overlapArea >= circleAreaAB) { distanceAB = radiusB+radiusA-i; break; }
				}
			}
		} else {
			radiusA = 100;
			radiusB = 100;
			circleAreaA = getCircleArea(radiusA);
			circleAreaB = getCircleArea(radiusB);
			circleAreaAB = (valueAB/valueA)*circleAreaA;
			if (Math.min(valueA, valueB)==valueAB) {
				distanceAB = Math.max(radiusA, radiusB) - Math.min(radiusA, radiusB);
			} else {
				for (int i=0; i<=(radiusB*2); i++) {
					overlapArea = getOverlapArea(radiusA, radiusB, radiusA+radiusB-i);
					if (overlapArea >= circleAreaAB) { distanceAB = radiusA+radiusB-i; break; }
				}
			}
		}
		if (distanceAB==0.0) { distanceAB = Math.max(radiusA, radiusB) - Math.min(radiusA, radiusB); }
		return distanceAB;
	}

	public static double getDistance(double valueA, double valueB, double valueAB, double maxR) {
		double radiusA = 0;
		double radiusB = 0;
		double distanceAB = 0;
		double circleAreaA = 0;
		double circleAreaB = 0;
		double circleAreaAB = 0;
		double overlapArea = 0;
		if (valueA>valueB) {
			radiusA = maxR;
			circleAreaA = getCircleArea(radiusA);
			circleAreaB = (valueB/valueA)*circleAreaA;
			circleAreaAB = (valueAB/valueA)*circleAreaA;
			radiusB = getCircleRadius(circleAreaB);
			if (Math.min(valueA, valueB)==valueAB) {
				distanceAB = Math.max(radiusA, radiusB) - Math.min(radiusA, radiusB);
			} else {
				for (int i=0; i<=(radiusB*2); i++) {
					overlapArea = getOverlapArea(radiusA, radiusB, radiusA+radiusB-i);
					distanceAB = radiusA+radiusB-i;
					if (overlapArea > circleAreaAB) {  break; }
				}
			}
		} else if (valueA<valueB) {
			radiusB = maxR;
			circleAreaB = getCircleArea(radiusB);
			circleAreaA = (valueA/valueB)*circleAreaB;
			circleAreaAB = (valueAB/valueB)*circleAreaB;
			radiusA = getCircleRadius(circleAreaA);
			if (Math.min(valueA, valueB)==valueAB) {
				distanceAB = Math.max(radiusA, radiusB) - Math.min(radiusA, radiusB);
			} else {
				for (int i=0; i<=(radiusA*2); i++) {
					overlapArea = getOverlapArea(radiusB, radiusA, radiusB+radiusA-i);
					if (overlapArea > circleAreaAB) { distanceAB = radiusB+radiusA-i; break; }
				}
			}
		} else {
			radiusA = maxR;
			radiusB = maxR;
			circleAreaA = getCircleArea(radiusA);
			circleAreaB = getCircleArea(radiusB);
			circleAreaAB = (valueAB/valueA)*circleAreaA;
			if (Math.min(valueA, valueB)==valueAB) {
				distanceAB = Math.max(radiusA, radiusB) - Math.min(radiusA, radiusB);
			} else {
				for (int i=0; i<=(radiusB*2); i++) {
					overlapArea = getOverlapArea(radiusA, radiusB, radiusA+radiusB-i);
					if (overlapArea > circleAreaAB) { distanceAB = radiusA+radiusB-i; break; }
				}
			}
		}
		if (distanceAB==0.0) { distanceAB = Math.max(radiusA, radiusB) - Math.min(radiusA, radiusB); }
		return distanceAB;
	}
}



