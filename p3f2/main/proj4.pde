class ballSystem{
	staticPillars pillars; // collection of all pillars (extension of particles[])
	movingballs balls; //collection of all balls (extension of particles[])

	ballSystem(){}
	ballSystem(staticPillars pillars){this.pillars=pillars;}
	ballSystem(movingballs balls){this.balls=balls;}
	ballSystem(movingballs balls, staticPillars pillars){this.balls=balls; this.pillars=pillars;}

	//set pillars from pts Q
	void setPillars(pts Q,float rb, float h){
		this.pillars=new staticPillars(Q,rb,h);
	}

	//create one test ball
	void createSingleTestBall(){
		this.balls=new movingballs();
		balls.createSingleTestBall();
	}

	//calculate all balls trace in one frame
	void frameCalculation(){
		balls.balls[0].simpleMove(1);
	}

	//call calculation function and draw
	void frameDraw(){
		frameCalculation();
		fill(green);
		pillars.draw();
		fill(red);
		balls.draw();
	}
}


class movingballs{
	particle[] balls;
	int num=0;

	movingballs(){}

	void createSingleTestBall(){
		this.num=1;
		balls=new particle[1];
		balls[0]=new particle(P(100,100,0));
		balls[0].asMovingBall();
		balls[0].setRadius(20);
		balls[0].setSpeed(2);
	}

	// void simpleMove(float frameTime){
	// }

	void draw(){for(int i=0;i<num;i++) balls[i].draw();}
}

class staticPillars{
	particle[] pillars;
	int num=0;

	staticPillars(){}

	//create pillar from pts Q
	staticPillars(pts Q,float rb, float h){
		this.num=Q.nv;
		pillars=new particle[num];
		for (int i=0;i<num;i++){
			pillars[i]=new particle(Q.G[i]);
			pillars[i].asPillar();
			pillars[i].setHeight(h);
			pillars[i].setRadius(rb);
		}
	}

	void draw(){for(int i=0;i<num;i++) pillars[i].draw();}
}