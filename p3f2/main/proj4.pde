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

	void createDoubleTestBall(){
		this.balls=new movingballs();
		balls.createDoubleTestBall();
	}

	//using current balls and pillars position, calculate next minimum collision time, it will return min(1, next collision time)
	//this method will also set global variable cursor_ball0, cursor_ball1, cursor_pillar; When two ball hits each other or ball pillar hit, the cursor will set and remain one will be -1
	float nextCollisionTime(){
		float minTime=1;
		this.cursor_ball0=-1;
		this.cursor_ball1=-1;
		this.cursor_pillar=-1;
		//ball-ball check
		for (int i=0;i<balls.num;i++){
			for (int j=i+1;j<balls.num;j++){
				boolean potential=potentialCollisionDetection(balls.balls[i],balls.balls[j]);
				if (potential){
					float nextTime=twoParticleCrossTime(balls.balls[i],balls.balls[j]);
					if (nextTime<minTime && nextTime>=0){
						minTime=nextTime;
						cursor_ball0=i;
						cursor_ball1=j;
						cursor_pillar=-1;
					}
				}
			}
		}
		//ball-pillar check
		for (int i=0;i<balls.num;i++){
			for (int j=0;j<pillars.num;j++){
				boolean potential=potentialCollisionDetection(balls.balls[i],pillars.pillars[j]);
				if (potential){
					float nextTime=twoParticleCrossTime(balls.balls[i],pillars.pillars[j]);
					//float nextTime=ball_pillar_collisionDetection(balls.balls[i],pillars.pillars[j]);
					if (nextTime<minTime && nextTime>=0){
						minTime=nextTime;
						cursor_ball0=i;
						cursor_ball1=-1;
						cursor_pillar=j;
					}
				}
			}
		}
		return minTime;
	}

	//calculate all balls trace in one frame
	void frameCalculation(){
		float accumulateTime=0;
		float t;
		//System.out.println("=======");
		while (true){
			t=nextCollisionTime(); // get closed next collision time
			//System.out.println(t);
			if (accumulateTime+t>=1){balls.simpleMoveAll(1-accumulateTime); break;} //next collision not ccur in this frame
			balls.simpleMoveAll(t); // all balls move time t (no collision between t)
			if (this.cursor_ball1==-1){
				//this means the next collision happens between ball and pillar
				ballPillarRebound(balls.balls[this.cursor_ball0],pillars.pillars[this.cursor_pillar]);
			}
			if (this.cursor_pillar==-1){
				//this means the next collision happens between two balls
				ballBallRebound(balls.balls[this.cursor_ball0],balls.balls[this.cursor_ball1]);
				// System.out.println("----");
				// System.out.println(this.cursor_ball0);
				// System.out.println(balls.balls[this.cursor_ball0].v);
				// System.out.println(balls.balls[this.cursor_ball1].v);
				// System.out.println("----");
			}
			accumulateTime+=t;//get accumulate time

		}

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

	void createDoubleTestBall(){
		this.num=2;
		balls=new particle[this.num];
		for (int i=0;i<this.num;i++){
			balls[i]=new particle(P(i*500+300,i*500+300,0));
			balls[i].asMovingBall();
			balls[i].setRadius(20);
			balls[i].setSpeed(3);
		}
		balls[1].v=V(-1,-1,0);
		balls[1].directionVecNorm(true);
		balls[1].setSpeed(5);
		balls[0].setSpeed(5);
		balls[0].setMass(1);
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