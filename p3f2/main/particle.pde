class particle{
	pt p=P();			       //particle position
	vec v=new vec();       //particle moving direction, assume unit vector. If not, "directionNorm" can trim the vector to unit and increase the speed
	float radius=1;        //particle radius
	float speed=10;        //particle moving speed, -1 means inf (can be used for photon)
	float mass=1;          //particle mass, 0 means no mass (for photon), -1 means inf mass (for pillar), 1 for default ball mass
	boolean isMoving=true; //if particle can move, isMoving = true: ball or photon; isMoving = false: pillar
	float drawScale=1;     //when drawing, draw size = radius * drawScale;
	float height=80;       //this is only used when particle considered as pillar

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
	
	//set particle as photon
	particle asPhoton(){
		this.radius=0;
		this.v=V(1,1,0);
		this.mass=0;//photon has no mass
		this.isMoving=true;
		this.drawScale=5;//for visualization
		return this;
	}

	//set particle as ball
	particle asMovingBall(){
		this.isMoving=true;
		this.v=V(1,1,0);
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
	void directionNorm(boolean isSpeedModified){
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




boolean potentialCollisionDetection(particle A, particle B){
	//quck detect whether two particle is far enought which will not cause collision in one frame
	if ((A.isMoving==false) && (B.isMoving==false)) return false; //both pillars;
	if ((A.speed==-1) || (B.speed==-1)) return true;// one particle is photon;
	float distance=d(A.p,B.p);
	float maxMovement=A.speed+B.speed+A.radius+B.radius;
	if (distance>maxMovement) return false;
	else return true;
}