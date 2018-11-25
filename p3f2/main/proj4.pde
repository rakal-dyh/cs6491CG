class ballSystem{
	staticPillars pillars; // collection of all pillars (extension of particles[])
	movingballs balls; //collection of all balls (extension of particles[])

	int cursor_ball0=-1;
	int cursor_ball1=-1;
	int cursor_pillar=-1;

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

	//using current balls and pillars position, calculate next minimum collision time, it will return min(1, next collision time)
	float nextCollisionTime(){
		float minTime=1;
		this.cursor_ball0=-1;
		this.cursor_ball1=-1;
		this.cursor_pillar=-1;
		//ball-ball check
		for (int i=0;i<balls.num;i++){
			//pass now
		}
		//ball-pillar check{
		for (int i=0;i<balls.num;i++){
			for (int j=0;j<pillars.num;j++){
				boolean potential=potentialCollisionDetection(balls.balls[i],pillars.pillars[j]);
				if (potential){
					float nextTime=twoParticleCrossTime(balls.balls[i],pillars.pillars[j]);
					//float nextTime=ball_pillar_collisionDetection(balls.balls[i],pillars.pillars[j]);
					if (nextTime<minTime && nextTime>=0){
						minTime=nextTime;
						cursor_ball0=i;
						cursor_pillar=j;
					}
				}
			}
		}
		return minTime;
	}

	//calculate all balls trace in one frame
	void frameCalculation(){
		//System.out.println("----");
		// boolean move=true;
		// for (int i=0;i<pillars.num;i++){
		// 	boolean potential=potentialCollisionDetection(balls.balls[0],pillars.pillars[i]);
		// 	//System.out.println(potential);
		// 	if (potential){
		// 		//float coll=ball_pillar_collisionDetection(balls.balls[0],pillars.pillars[i]);
		// 		// System.out.println(balls.balls[0]);
		// 		float coll2=twoParticleCrossTime(balls.balls[0],pillars.pillars[i]);
		// 		// System.out.println(balls.balls[0]);
		// 		// System.out.println(coll);
		// 		// System.out.println(coll2);
		// 		if ((coll2<1) && (coll2>=0)) balls.balls[0].simpleMove(coll2);
		// 		if (coll2<0) move=false;
		// 	}
		// }
		// if (move) balls.balls[0].simpleMove(1);
		boolean move=true;
		float t=nextCollisionTime();
		//System.out.println(t);
		if (t>=1) balls.balls[0].simpleMove(t);
		if ((t<1) && (t>=0)){
			//System.out.println(balls.balls[0].v);
			balls.balls[0].simpleMove(t);
			vec reboundDirection=ballPillarRebound(balls.balls[this.cursor_ball0],pillars.pillars[this.cursor_pillar]);
			balls.balls[this.cursor_ball0].v=reboundDirection;
			System.out.println(balls.balls[0].p);
			balls.balls[0].simpleMove(1-t);
			System.out.println(balls.balls[0].p);
			System.out.println(twoParticleCrossTime(balls.balls[this.cursor_ball0],pillars.pillars[this.cursor_pillar]));
			//System.out.println(balls.balls[0].v);
		}
		// if (t<0) move=false;
		// if (move) balls.balls[0].simpleMove(1);
		// if (this.cursor_ball0>=0) ballPillarRebound(balls.balls[this.cursor_ball0],pillars.pillars[this.cursor_pillar]);
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

	//move all balls in line by given frameTime
	void simpleMoveAll(float frameTime){
		for(int i=0;i<num;i++) balls[i].simpleMove(frameTime);
	}

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
			pillars[i].p.z=0;
			pillars[i].asPillar();
			pillars[i].setHeight(h);
			pillars[i].setRadius(rb);
		}
	}

	void draw(){for(int i=0;i<num;i++) pillars[i].draw();}
}