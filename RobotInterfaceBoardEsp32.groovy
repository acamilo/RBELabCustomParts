class BoardMaker{
	static double radius = 1
	static double insertionDepthOfWii = 7.1
	static double wiiCenterOffset=2.2
	static double wiiConnectorThickness = 7.40
	static double wiiConnectorWidth = 12.21
	static double wiiInsetFromEdge = 12.42+(wiiConnectorWidth/2)
	static double boardX =67.35
	static double boardY =75.0
	static double boardZ =1.67
	static double cutoutRadius=4
	static double cutoutDepth = 7.3
	static double ioKaX =63.7
	static double ioKaY =48.79
	static double ioKaZ =17.67
	static double lowerKeepaway = 6.3
	static double holeCornerInset = 1.8+1.5
	static double boardConnects = 2.3
	static double usbThickness = 6.75
	static double wireHeight = 5
	static double wireRadius = 1.5
	static double negativeWireOffset = 3-3.5
	static double positiveWireOffset = 7+3.5
	static double caseOutSet = 4
	static double powerKeepawayOffset=53.21
	static double usbHeight=11.06
	static LengthParameter	printerOffset 		= new LengthParameter("Printer Offset",0.6,[1.75,0])

	def makeWiiConnector(){
		CSG wiiConnect = new RoundedCube(	wiiConnectorWidth,// X dimention
					wiiConnectorThickness,// Y dimention
					8.11+(radius)//  Z dimention
					).cornerRadius(radius)// sets the radius of the corner
					.toCSG()
					.toZMin()
					.movez(-radius)
		
		CSG cutout = new RoundedCube(	5,// X dimention
					1.58+radius,// Y dimention
					8.11+(radius*2)//  Z dimention
					)
					.cornerRadius(radius)// sets the radius of the corner
					.toCSG()
					.toZMin()
					.movez(-radius)
					.toYMax()
					.movey(radius)
					.movey(wiiConnect.getMaxY())
		CSG wiiBody = new Cube(	20.27,// X dimention
					13,// Y dimention
					23.4//  Z dimention
					)
					.toCSG()
					.toZMax()
		CSG wiiClip = new Cube(	17.9,// X dimention
					4.62,// Y dimention
					5//  Z dimention
					)
					.toCSG()
					.toZMax()	
					.toYMax()
					.movey(wiiBody.getMinY())	
		CSG wiiNotch = new Cube(	14.75,// X dimention
					5,// Y dimention
					2//  Z dimention
					)
					.toCSG()
					.toZMin()
					.movez(3.3)
					.movey(-0.5)				
		wiiConnect=wiiConnect
				.difference(cutout)		
				.union([wiiBody,wiiClip,wiiNotch])
				
				
		return wiiConnect	
	}
	def makeBoard(){
		CSG wiiConnect = makeWiiConnector()	
		wiiConnect=wiiConnect
				.toZMax()
				.movez(insertionDepthOfWii)
				.movey(wiiConnectorThickness/2-wiiCenterOffset)		
				.rotx(-90)
				.rotz(180)
				.movex(wiiInsetFromEdge)
				.movez(-boardZ)
							
		CSG mainBoard = new Cube(boardX+boardConnects*2,boardY+boardConnects,boardZ).toCSG()
						.toXMin()
						.toYMin()
						.toZMax()
						.move(-boardConnects,-boardConnects,0)
		
		CSG mainBoardCutout = new RoundedCube(33.0,cutoutDepth+cutoutRadius,boardZ+cutoutRadius*2)
						.cornerRadius(cutoutRadius)
						.toCSG()
						.toYMax()
						.toZMax()	
						.movez(cutoutRadius)
						.movey(mainBoard.getMaxY()+cutoutRadius)
						.movex(boardX/2)
		mainBoard=mainBoard.difference(mainBoardCutout)
		
		CSG antenna = new Cube(21.85,7,13.69+boardZ+lowerKeepaway).toCSG()
					.toYMax()
					.toZMin()
					.movey(-ioKaY)
					.movex(ioKaX/2)
		CSG IOkeepaway = new Cube(ioKaX,ioKaY,ioKaZ+boardZ+lowerKeepaway).toCSG()
						.toXMin()
						.toYMax()
						.toZMin()
						.union(antenna)
						.movez(-(boardZ+lowerKeepaway))
						.movey(-cutoutDepth+boardY)
						.movex(1.8)
		CSG fusekeepaway = new Cube(24,9,ioKaZ+boardZ+lowerKeepaway+10).toCSG()
						.toXMin()
						.toYMin()
						.toZMin()
						.movez(-(boardZ+lowerKeepaway))
						.movey(IOkeepaway.getMinY()-1.9)
						.movex(1.63)
		CSG electronicsKeepaway = new Cube(30,19.4,3.2+(boardZ+lowerKeepaway)).toCSG()
						.toXMin()
						.toYMin()
						.toZMin()
						.movez(-(boardZ+lowerKeepaway))
						.movex(fusekeepaway.getMaxX()-6)
						.difference(wiiConnect.intersect(wiiConnect.getBoundingBox().toYMin()).hull())
		CSG switchkeepaway = new Cube(17,10.6,ioKaZ+boardZ+lowerKeepaway+10).toCSG()
						.toXMax()
						.toYMin()
						.toZMin()
						.movez(-(boardZ+lowerKeepaway))
						.movey(6.81)
						.movex(boardX-1.63)
		CSG holeLower = new Cylinder(1.25,1.25,ioKaZ,(int)20).toCSG()
					.movez(-ioKaZ)
		CSG holeUpper = new Cylinder(1.5,1.5,ioKaZ,(int)10).toCSG()
		CSG holeHead = new Cylinder(3.1,3.1,ioKaZ,(int)20).toCSG()
					.movez(7)			
		def hole = holeUpper.union([holeLower,holeHead])
		mainBoard=mainBoard
			.union(hole.move(holeCornerInset,holeCornerInset,0))
			.union(hole.move(holeCornerInset,boardY-holeCornerInset,0))
			.union(hole.move(boardX-holeCornerInset,boardY-holeCornerInset,0))
			.union(hole.move(boardX-holeCornerInset,holeCornerInset,0))
		
		CSG wire = new Cylinder(wireRadius,40).toCSG()
				.movez(-wireRadius)
				.rotx(-90)
				.movez(wireHeight)
		CSG wirekeepaway = new Cube(positiveWireOffset+wireRadius*6,boardConnects,wireHeight).toCSG()
							.toXMax()
							.toZMin()
							.toYMax()
							.movex(wireRadius+3.5)
		CSG powerkeepaway = new Cube(17.84,19.79,10.43+boardZ+lowerKeepaway).toCSG()
						.toXMax()
						.movex(3.5)
						.toYMin()
						.toZMin()
						.movez(-(boardZ+lowerKeepaway))
						.union(wire.movex(-negativeWireOffset))
						.union(wire.movex(-positiveWireOffset))
						.union(wirekeepaway)
						.movex(powerKeepawayOffset)
		CSG usbCord = new Cylinder(2.0,40).toCSG()
						.rotx(90)
						.movez(usbThickness/2)
		CSG usbCordkeepaway = new RoundedCube(7,1,usbThickness)
						.cornerRadius(1)
						.toCSG()
						.toYMax()
						.toZMin()
						.movey(27.75)
						
		CSG usbkeepaway = new RoundedCube(11.05,12.6,usbThickness)
						.cornerRadius(1)
						.toCSG()
						.toYMin()
						.toZMin()
						.union(usbCordkeepaway)
						.hull()
						.union(usbCord)
						.movey(-cutoutDepth+boardY)
						.movex(boardX/2)
						.movez(usbHeight)
		//return electronicsKeepaway
		return [wiiConnect,mainBoard,IOkeepaway,switchkeepaway,powerkeepaway,usbkeepaway,fusekeepaway,electronicsKeepaway]
	}
	def makeCase(){
		def board =makeBoard()
		double caseRounding = 1
		double frontCaseDepth = -cutoutDepth+boardY-ioKaY-caseRounding+3
		double lowerCaseDepth = Math.abs(board[0].getMinZ())
		
		CSG wirekeepaway = new RoundedCube(positiveWireOffset+wireRadius*8,caseOutSet-boardConnects,wireHeight+caseRounding*2)
							.cornerRadius(caseRounding)
							.toCSG()
							.toXMax()
							.toZMin()
							.toYMin()
							.movez(-caseRounding*2)
							.movex(wireRadius*3)	
							.movey(-caseOutSet)
							.movex(powerKeepawayOffset)	
		double LugX = boardX+(boardConnects*2)+caseOutSet*2
		CSG basicLug = new RoundedCube(LugX,frontCaseDepth,lowerCaseDepth)
						.cornerRadius(caseRounding)
						.toCSG()
						.toZMax()
						.toYMin()
						.movey(-caseOutSet)
						.toXMin()
						.movex(-caseOutSet-boardConnects)
						
					
		CSG fullBoard = CSG.unionAll(board)
		
						
						
		
		double backeOfCaseY = boardY-	cutoutDepth
		CSG usbkeepaway = new RoundedCube(13+caseRounding*2,frontCaseDepth,usbHeight+caseRounding*2+usbThickness/2)
							.cornerRadius(caseRounding)
							.toCSG()
							.toZMin()
							.movez(-caseRounding*2)
							.toYMin()
							.movex(boardX/2)
							
		
		CSG backBottom = basicLug
						.toYMin()
						.movey(backeOfCaseY)
						.union(basicLug)
						.hull()
						.union(usbkeepaway.movey(backeOfCaseY))
						.union(	wirekeepaway)	
						.difference(fullBoard)
		double vexGrid = 1.0/2.0*25.4
		CSG vexMount = Vitamins.get( "vexFlatSheet","Aluminum 1x5")		
						.intersect(new Cube(vexGrid*7.5).toCSG())
						.rotz(-90)
						.movey(	-caseOutSet+caseRounding+vexGrid/2)	
						.movex(-caseOutSet+caseRounding-boardConnects-vexGrid/2)
						.movez(		backBottom.getMinZ())	
		CSG vexMountB = vexMount.movex(vexGrid*7)
						.union(	vexMount)				
		CSG backVex = vexMountB
						.movey(vexGrid*3)
		CSG allvexbits = vexMountB.union(backVex)
						.toYMin()
						.movey(backBottom.getMinY())	
		backBottom=	backBottom	
			.union(allvexbits)
		println "Performing keepaway opperation ..."
		def fullBoardMink =CSG.unionAll(fullBoard.minkowski(new Cube(printerOffset.getMM()).toCSG()))
		def backBottomMink =CSG.unionAll(backBottom.minkowski(new Cube(printerOffset.getMM()).toCSG()))
		println "keepaway Done!"
		CSG frontTop = basicLug	
						.scalez(2)
						.toZMin()
						.movez(-caseRounding*2)
						.difference(fullBoardMink)
						.difference(backBottomMink)	
		CSG backTop = basicLug	
						.scalez(2)
						.toZMin()
						.movez(-caseRounding*2)
						.toYMin()
						.movey(backeOfCaseY)
						.difference(fullBoardMink)	
						.difference(backBottomMink)	
		CSG bottom = backBottom			
		bottom.setManufacturing({ toMfg ->
			return toMfg
					.toXMin()
					.toYMin()
					.toZMin()
		})	
		frontTop.setManufacturing({ toMfg ->
			return toMfg
					.toXMin()
					.toYMin()
					.roty(180)
					.toZMin()
		})		
		backTop.setManufacturing({ toMfg ->
			return toMfg
					.toXMin()
					.toYMin()
					.roty(180)
					.toZMin()
		})										
		def caseParts = [bottom,frontTop,backTop]
		return caseParts
		board.addAll(caseParts)
		return board
	}
}
return new BoardMaker().makeCase()