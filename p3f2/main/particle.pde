class particle{
	pt p=P();			       //particle position
	vec v=new vec();       //particle moving direction, assume unit vector. If not, "directionNorm" can trim the vector to unit and increase the speed
	float radius=1;        //particle radius
	float speed=10;        //particle moving speed, -1 means inf (can be used for photon)
	float mass=1;          //particle mass, 0 means no mass (for photon), -1 means inf mass (for pillar), 1 for default ball mass
	boolean isMoving=true; //if particle can move, isMoving = true: ball or photon; isMoving = false: pillar
	float drawScale=1;     //when drawing, draw size = radius * drawScale;
	float height=80;       //this is only used when particle considered as pillar

	particle(pt pos, vec v, float r, float speed, float mass, boolean isMoving, float ds, float h){ this.p.set(pos); this.v.set(v); this.radius=r; this.speed=speed; this.mass=mass; this.isMoving=isMoving; this.drawScale=ds; this.height=h;}
	particle(pt pos, vec v, float r, float speed, float mass, boolean isMoving, float ds){ this.p.set(pos); this.v.set(v); this.radius=r; this.speed=speed; this.mass=mass; this.isMoving=isMoving; this.drawScale=ds;}
	particle(pt pos, vec v, float r, float speed, float mass, boolean isMoving){ this.p.set(pos); this.v.set(v); this.radius=r; this.speed=speed; this.mass=mass; this.isMoving=isMoving;}
	particle(pt pos, vec v, float r, float speed, float mass){ this.p.set(pos); this.v.set(v); this.radius=r; this.speed=speed; this.mass=mass;}
	particle(pt pos, vec v, float r, float speed){ this.p.set(pos); this.v.set(v); this.radius=r; this.speed=speed;}
	particle(pt pos, vec v, float r){ this.p.set(pos); this.v.set(v); this.radius=r;}	
	particle(pt pos, vec v){ this.p.set(pos); this.v.set(v);}
	particle(pt pos){ this.p.set(pos);}		
	particle(){}

	void setPt(pt p){this.p.set(p);}
	void setVec(vec v){this.v.set(v);}
	void setRadius(float r){this.radius=r;}
	void setSpeed(float s){this.speed=s;}
	void setMass(float m){this.mass=m;}
	void setisMoving(boolean isMoving){this.isMoving=isMoving;}
	void setDrawScale(float s){this.drawScale=s;}
	void setHeight(float h){this.height=h;}

	particle copy(){return new particle(this.p, this.v, this.radius, this.speed, this.mass, this.isMoving, this.drawScale, this.height);}
	
	//set particle as photon
	particle asPhoton(){
		this.radius=0;
		this.v=V(1,1,0);
		this.mass=0;//photon has no mass
		this.isMoving=true;
		this.drawScale=5;//for visualization
		directionVecNorm(true);
		return this;
	}

	//set particle as ball
	particle asMovingBall(){
		this.isMoving=true;
		this.v=V(1,1,0);
		directionVecNorm(true);
		return this;
	}

	//set particle as pillar
	particle asPillar(){
		this.v=V();
		this.speed=0;
		this.mass=-1;
		this.isMoving=false;
		return this;
	}

	//norm the direction vector, and the speed value will increase by length of vector or not, which based on given boolean parameter
	void directionVecNorm(boolean isSpeedModified){
		float length=this.v.norm();
		v=U(v);
		if (length<0.000001) isSpeedModified=false; //the direction vector is not set
		if (isSpeedModified) this.speed=this.speed*length;
	}

	//the particle moves in line with direction vector, current location and speed. frameTime in [0,1], 1 means end of this frame
	//x=x+dx*speed*frameTime; y=y+dy*speed*frameTime; 2D motion
	particle simpleMove(float frameTime){
		this.p.x=this.p.x+v.x*speed*frameTime;
		this.p.y=this.p.y+v.y*speed*frameTime;
		return this;
	}

	void draw(){
		if (isMoving) show(p,radius*drawScale); //draw ball or photon
		else pillar(p,height,radius*drawScale); //draw pillar
	}
}



//quick detect whether two particle is far enought which will not cause collision in one frame
boolean potentialCollisionDetection(particle A, particle B){
	if ((A.isMoving==false) && (B.isMoving==false)) return false; //both pillars;
	if ((A.speed==-1) || (B.speed==-1)) return true;// one particle is photon;
	float distance=d(A.p,B.p);
	//System.out.println(distance);
	float maxMovement=A.speed+B.speed+A.radius+B.radius;
	//System.out.println(maxMovement);
	if (distance>maxMovement) return false;
	else return true;
}

//calculate the time of ball-pillar collision time, if two particles never touchs each other return -1. Here always assume A is ball and B is pillar. So if both are balls will print("error") and return -2
float ball_pillar_collisionDetection(particle A, particle B){
	if ((A.isMoving==false) && (B.isMoving==false)) {System.out.println("-ball_pillar_collisionDetection- Warning: both pillars, return -1"); return -1;} //both pillars;
	if ((A.isMoving==true) && (B.isMoving==true)) {System.out.println("-ball_pillar_collisionDetection- Warning: both balls, return -2"); return -2;} //both pillars;
	if (B.isMoving==true){
		//B is ball and A is pillar, exchange A and B
		particle C=A.copy();
		A=B;
		B=C;
	}

	float effRadius=A.radius+B.radius; //in calculation, the ball radius was added to the pillar.
	float dx=A.v.x; float dy=A.v.y;
	vec nva=V(-dy,dx,0);

	pt crossPoint;
	float bToCrossDistance;
	float aToCrossDistance;

	twoLineCross CrossInfo=new twoLineCross(A.p,A.v,B.p,nva);
	crossPoint=CrossInfo.twoLineCorssPoint;
	bToCrossDistance=d(B.p,crossPoint);
	aToCrossDistance=d(A.p,crossPoint);

	if (effRadius<bToCrossDistance) return -1; //two paricle never touches each other
	else{
		float halfArcLen=sqrt(effRadius*effRadius-bToCrossDistance*bToCrossDistance);
		float moveDistance=aToCrossDistance-halfArcLen;
		return moveDistance/A.speed;
	}
	
}

class twoLineCross{
	pt twoLineCorssPoint;
	boolean isParallel;
	twoLineCross(pt A, vec va, pt B, vec vb){
		va=U(va); vb=U(vb);
		float x1,y1,x2,y2,dx1,dx2,dy1,dy2,px,py;
		dx1=va.x; dy1=va.y;
		dx2=vb.x; dy2=vb.y;
		if ((dx1==dx2) && (dy1==dy2)) isParallel=true;
		if ((dx1==0) && (dx2==0)) isParallel=true;
		if ((dy1==0) && (dy2==0)) isParallel=true;
		if (isParallel) twoLineCorssPoint=P();
		else{
			if ((dx1==0) || (dx2==0)){
				if (dx2==0){
					float tmp;
					tmp=dx1; dx1=dx2; dx2=tmp;
					tmp=dy1; dy1=dy2; dy2=tmp;
					x1=B.x; y1=B.y;
					x2=A.x; y2=A.y;
				}
				else{
					x1=A.x; y1=A.y;
					x2=B.x; y2=B.y;
				}
				px=x1;
				py=(dy2/dx2)*px-(dy2/dx2)*x2+y2;
				twoLineCorssPoint=P(px,py);
			}
			else{
				x1=A.x; y1=A.y;
				x2=B.x; y2=B.y;
				float a,b,c,d;
				a=dy1/dx1; b=dy2/dx2; c=-a*x1+y1; d=-b*x2+y2;
				px=(d-c)/(a-b);
				py=a*px+c;
				twoLineCorssPoint=P(px,py);
			}
		}
	}
}

//given two 2d lines, return the cross point
