class particle{
	pt p=P();			   //particle position
	vec v=new vec();       //particle moving direction, assume unit vector. If not, "directionNorm" can trim the vector to unit and increase the speed
	float radius=1;        //particle radius
	float speed=10;        //particle moving speed, -1 means inf (can be used for photon)
	float mass=1;          //particle mass, 0 means no mass (for photon), -1 means inf mass (for pillar), 1 for default ball mass
	boolean isMoving=true; //if particle can move, isMoving = true: ball or photon; isMoving = false: pillar
	float drawScale=1;     //when drawing, draw size = radius * drawScale;
	float height=80;       //this is only used when particle considered as pillar
	int hitNumber=0;     //How many time this particle hit other paricles
	float energy=0;        //photon engergy

	particle(pt pos, vec v, float r, float speed, float mass, boolean isMoving, float ds, float h, int hit, float e){ this.p.set(pos); this.v.set(v); this.radius=r; this.speed=speed; this.mass=mass; this.isMoving=isMoving; this.drawScale=ds; this.height=h; this.hitNumber=hit; this.energy=e;}
	particle(pt pos, vec v, float r, float speed, float mass, boolean isMoving, float ds, float h){ this.p.set(pos); this.v.set(v); this.radius=r; this.speed=speed; this.mass=mass; this.isMoving=isMoving; this.drawScale=ds; this.height=h;}
	particle(pt pos, vec v, float r, float speed, float mass, boolean isMoving, float ds){ this.p.set(pos); this.v.set(v); this.radius=r; this.speed=speed; this.mass=mass; this.isMoving=isMoving; this.drawScale=ds;}
	particle(pt pos, vec v, float r, float speed, float mass, boolean isMoving){ this.p.set(pos); this.v.set(v); this.radius=r; this.speed=speed; this.mass=mass; this.isMoving=isMoving;}
	particle(pt pos, vec v, float r, float speed, float mass){ this.p.set(pos); this.v.set(v); this.radius=r; this.speed=speed; this.mass=mass;}
	particle(pt pos, vec v, float r, float speed){ this.p.set(pos); this.v.set(v); this.radius=r; this.speed=speed;}
	particle(pt pos, vec v, float r){ this.p.set(pos); this.v.set(v); this.radius=r;}	
	particle(pt pos, vec v){ this.p.set(pos); this.v.set(v);}
	particle(pt pos){ this.p.set(pos);}	
	particle(pt pos,int hit){this.p.set(pos); this.hitNumber=hit;}	
	particle(){}

	void setPt(pt p){this.p.set(p);}
	void setVec(vec v){this.v.set(v);}
	void setRadius(float r){this.radius=r;}
	void setSpeed(float s){this.speed=s;}
	void setMass(float m){this.mass=m;}
	void setisMoving(boolean isMoving){this.isMoving=isMoving;}
	void setDrawScale(float s){this.drawScale=s;}
	void setHeight(float h){this.height=h;}

	particle copy(){return new particle(this.p, this.v, this.radius, this.speed, this.mass, this.isMoving, this.drawScale, this.height, this.hitNumber, this.energy);}
	
	//set particle as photon
	particle asPhoton(){
		this.radius=0;
		this.v=V(1,1,0);
		this.mass=0;//photon has no mass
		this.isMoving=true;
		this.drawScale=5;//for visualization
		this.energy=10;
		directionVecNorm(true);
		return this;
	}

	particle asPhoton(vec v){
		this.radius=0;
		this.v=v;
		this.mass=0;//photon has no mass
		this.isMoving=true;
		this.drawScale=5;//for visualization
		this.energy=10;
		this.speed=1000;
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
		if (isMoving) {
		float c=250-this.hitNumber*10;
		fill(c,c,c);
		show(p,radius*drawScale); //draw ball or photon
		}
		else {
		float c=250-this.hitNumber*100;
		fill(c,c,c);
		pillar(p,height,radius*drawScale); //draw pillar
		}
	}

	@Override
	String toString(){
		return (Float.toString(p.x)+","+Float.toString(p.y)+"; ");
	}
}



//quick detect whether two particle is far enought which will not cause collision in one frame
boolean potentialCollisionDetection(particle A, particle B){
	if ((A.isMoving==false) && (B.isMoving==false)) return false; //both pillars;
	if ((A.speed==-1) || (B.speed==-1)) return false;// one particle is photon; no collision
	float distance=d(A.p,B.p);
	float maxMovement=A.speed+B.speed+A.radius+B.radius;
	if (distance>maxMovement) return false;
	else return true;
}

//given two particles, either moving or static, return the time they will hit each other, 
float twoParticleCrossTime(particle A, particle B){
	float x0,y0,x1,y1,dx0,dy0,dx1,dy1,s0,s1,r;
	x0=A.p.x; y0=A.p.y;
	x1=B.p.x; y1=B.p.y;
	dx0=A.v.x; dy0=A.v.y; 
	dx1=B.v.x; dy1=B.v.y;
	s0=A.speed; s1=B.speed;
	r=A.radius+B.radius;
	float tx,dtx,ty,dty;//some temp value to simplify calculation
	tx=x0-x1;
	dtx=dx0*s0-dx1*s1;
	ty=y0-y1;
	dty=dy0*s0-dy1*s1;
	float a,b,c,delta;//at^2+bt+c=0, delta=b^2-4ac
	a=dtx*dtx+dty*dty;
	b=2*(tx*dtx+ty*dty);
	c=tx*tx+ty*ty-r*r;
	delta=b*b-4*a*c;
	if ((a==0) || (delta<0)){
		return -1;
	}
	else{
		return (-b-sqrt(delta))/(2*a);
	}
}

//given A as ball and B as pillar, assume A and B are already in attached threshold status, calculate the rebound direction of ball A and then reset it
vec ballPillarRebound(particle A, particle B){
	// float effRadius=A.radius+B.radius;
	// float distance=d(A.p,B.p);
	// float diff=effRadius-distance;
	//before are used to check whether given ball and pillar are closed to each other
	A.hitNumber+=1;
	B.hitNumber+=1;
	
	vec p2pVec=U(V(A.p,B.p));//vector from ball center point to pillar center point
	vec faceVec=U(Normal(p2pVec));
	float entryAngle=angle(A.v,faceVec);
	float fowardSpeed=n(A.v)*cos(entryAngle); //speed along the face
	float reboundSpeed=n(A.v)*sin(entryAngle); //speed normal to face
	
	vec fowardVec=V(fowardSpeed,faceVec);
	vec reboundVec=V(reboundSpeed,V(-1,p2pVec));
	A.v=U(A(fowardVec,reboundVec));
	return U(A(fowardVec,reboundVec));
}

//given two balls A and B, return the rebound direction of two balls; Considering mass effect, perfect elastic
vec[] ballBallRebound(particle A, particle B){
	// float effRadius=A.radius+B.radius;
	// float distance=d(A.p,B.p);
	// float diff=effRadius-distance;
	//before are used to check whether given ball and pillar are closed to each other
	A.hitNumber+=1;
	B.hitNumber+=1;

	vec[] endVec=new vec[2];
	vec p2pVec=U(V(A.p,B.p));//vector from ball center point to pillar center point
	vec faceVec=U(Normal(p2pVec));

	//System.out.println(p2pVec);
	//System.out.println(faceVec);

	float s1=A.speed;
	float s2=B.speed;

	float angle1=angle(A.v,faceVec);
	float angle2=angle(B.v,faceVec);
	float fowardSpeed1=n(A.v)*cos(angle1)*s1; //speed along the face of ball A
	float reboundSpeed1=n(A.v)*sin(angle1)*s1; //speed normal to face of ball A
	float fowardSpeed2=n(B.v)*cos(angle2)*s2; //speed along the face of ball B
	float reboundSpeed2=n(B.v)*sin(angle2)*s2; //speed normal to face of ball B

	// if (fowardSpeed1<0.00001) fowardSpeed1=0;
	// if (fowardSpeed2<0.00001) fowardSpeed2=0;

	//give the direction to rebound speed, define from point A to point B direction are position
	if (angle(p2pVec,A.v)>PI/2) reboundSpeed1=-reboundSpeed1;
	if (angle(p2pVec,B.v)>PI/2) reboundSpeed2=-reboundSpeed2;
	if ((abs(A.v.x+p2pVec.x)<0.00001) && (abs(A.v.y+p2pVec.y)<0.00001)) reboundSpeed1=-reboundSpeed1; //angle = NaN when actually angle = 0 or PI
	if ((abs(B.v.x+p2pVec.x)<0.00001) && (abs(B.v.y+p2pVec.y)<0.00001)) reboundSpeed2=-reboundSpeed2;

	float m1=A.mass;
	float m2=B.mass;

	float tmp=reboundSpeed1;
	reboundSpeed1=((m1-m2)*reboundSpeed1+2*m2*reboundSpeed2)/(m1+m2);
	reboundSpeed2=((m2-m1)*reboundSpeed2+2*m1*tmp)/(m1+m2);

	vec fowardVec1=V(fowardSpeed1,faceVec);
	vec reboundVec1=V(reboundSpeed1,p2pVec);
	endVec[0]=A(fowardVec1,reboundVec1);

	vec fowardVec2=V(fowardSpeed2,faceVec);
	vec reboundVec2=V(reboundSpeed2,p2pVec);
	endVec[1]=A(fowardVec2,reboundVec2);

	A.speed=n(endVec[0]);
	B.speed=n(endVec[1]);
	if (A.speed!=0) A.v=U(endVec[0]);
	//else A.v=V(1,0,0); //avoid zero vector
	if (B.speed!=0) B.v=U(endVec[1]);
	//else B.v=V(1,0,0);
	
	System.out.println(A.speed);
	System.out.println(B.speed);
	System.out.println("---");

	return endVec;
}


//depreciate
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

//depreciate
//given two lines (in point,vector format), return the cross point
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



