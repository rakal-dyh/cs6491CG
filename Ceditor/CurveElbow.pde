class CurveElbow{
  Elbow[] elbows;
  float[] twist_end_angles;
  PPath[] ppaths;
  boolean connectTailHead;
  boolean alignTailHead;
  boolean drawPPath;
  pts startCircle;

  CurveElbow(Elbow[] elbows, boolean connectTailHead, boolean alignTailHead, boolean drawPPath){
    this.connectTailHead = connectTailHead;
    this.elbows=elbows;
    this.drawPPath = drawPPath;
    startCircle = new pts();
    startCircle.declare();
    
    twist_end_angles = new float[elbows.length];
    calculate_and_twist_all_diffK();
    
    if (alignTailHead) {
      calculate_twist_end_angles();
      twist_end_angles();
    }
    
    initStartCircle();
    
    ppaths = new PPath[elbows.length];
    set_up_and_align_ppaths(PC.pv);
  }
  
  void initStartCircle() {
    for (int i = 0; i < elbows[0].num_of_circle_vectors; i++)
      startCircle.addPt(P(elbows[0].centers[0], elbows[0].circle_vectors[0][i]));
  }
  
  void set_up_and_align_ppaths(int start_index) {
    for (int i = 0; i < elbows.length; i++)
      ppaths[i] = new PPath(elbows[i], start_index);
    
    for (int i = 1; i < ppaths.length; i++)
      twist_adjacent_ppath(ppaths[i - 1], ppaths[i]);
  }
  
  void twist_adjacent_ppath(PPath p1, PPath p2) {
    float curr_diffK = calculate_tail_head_angle(p1, p2);
    if(curr_diffK != curr_diffK) curr_diffK = 0;
    PPath p2_copy = PP(p2);
    p2_copy.twist_all(curr_diffK);
    vec vec1 = p1.circle_vectors[p1.num_of_circles];
    vec vec2 = p2_copy.circle_vectors[0];
    
    if (norm(M(vec1, vec2))>0.001) p2.twist_all(-curr_diffK);
    else p2.twist_all(curr_diffK);
  }

  void calculate_and_twist_all_diffK(){
    for(int i=1;i<elbows.length;i++){
      twist_adjacent_elbows(elbows[i - 1], elbows[i]);
      
      /*
      if (true) { //norm(M(vec1, vec2))>0.01) {
        System.out.println(vec1.x + " " + vec1.y + " " + vec1.z);
        System.out.println(old_vec2.x + " " + old_vec2.y + " " + old_vec2.z);
        System.out.println(curr_diffK);
        System.out.println(vec1.x + " " + vec1.y + " " + vec1.z);
        System.out.println(vec2.x + " " + vec2.y + " " + vec2.z);
        float new_diffK = angle(old_vec2, vec2);
        System.out.println(new_diffK);
        System.out.println(angle(vec1, vec2));
        
        vec1n = V(vec1).normalize();
        old_vec2n = V(old_vec2).normalize();
        vec1nnormal = B(vec1n, old_vec2n);
        vec1nnormal.normalize();
        twisted = R(old_vec2, curr_diffK, vec1n, vec1nnormal);
        System.out.println(twisted.x + " " + twisted.y + " " + twisted.z);
        System.out.println(angle(vec1, twisted));
        
        System.out.println("-------");
      }
      */
      
      /*
      if (i==1){
        System.out.println(elbows[i-1].KS.x);
        System.out.println(elbows[i-1].KS.y);
        System.out.println(elbows[i-1].KS.z);
        System.out.println(elbows[i].KS.x);
        System.out.println(elbows[i].KS.y);
        System.out.println(elbows[i].KS.z);
        System.out.println(diffK[i]);
        System.out.println('-');
      }
      */
    }
  }
  
  void twist_adjacent_elbows(Elbow ea, Elbow eb) {
    float curr_diffK = calculate_tail_head_angle(ea, eb);
    if(curr_diffK!=curr_diffK) curr_diffK=0;
    Elbow eb_copy = E(eb);
    eb_copy.twist_first(curr_diffK);
    vec vec1 = ea.circle_vectors[ea.num_of_circles][0];
    if (ea.isTwisted) vec1 = ea.circle_vectors_twisted[ea.num_of_circles][0];
    vec vec2 = eb_copy.circle_vectors_twisted[0][0];
    
    if (norm(M(vec1, vec2))>0.001) eb.twist_all(-curr_diffK);
    else eb.twist_all(curr_diffK);
  }

  void draw(){
    drawTorusAroundStartCircle(elbows[0]);
    if (connectTailHead) {
      for(int i = 0; i < elbows.length; i++) {
        drawElbow(elbows[i]);
        if (drawPPath)
          drawP(ppaths[i]);
      }
    } else {
      for(int i = 0; i < elbows.length - 2; i++) {
        drawElbow(elbows[i]);
        if (drawPPath)
          drawP(ppaths[i]);
      }
    }
  }

  void calculate_twist_end_angles() {
    Elbow ea = elbows[elbows.length - 1];
    Elbow eb = elbows[0];
    float total_twist_end_angles = calculate_tail_head_angle(ea, eb);
    
    if(total_twist_end_angles!=total_twist_end_angles) total_twist_end_angles=0;
    Elbow ea_copy = E(ea);
    ea_copy.twist_all(total_twist_end_angles);
    vec vec1 = ea_copy.circle_vectors_twisted[ea_copy.num_of_circles][0];
    vec vec2 = eb.circle_vectors[0][0];
    if (eb.isTwisted) vec2 = eb.circle_vectors_twisted[0][0];
    
    if (norm(M(vec1, vec2))>0.001) total_twist_end_angles = -total_twist_end_angles;
    
    float total_length = 0;
    for (Elbow e : elbows) total_length += e.arcLength;
    for (int i = 0; i < elbows.length; i++) {
      twist_end_angles[i] = total_twist_end_angles * elbows[i].arcLength / total_length;
      if (twist_end_angles[i] != twist_end_angles[i]) twist_end_angles[i] = 0;
    }
    //System.out.println(Arrays.toString(twist_end_angles));
  }
  
  void twist_end_angles() {
    Elbow[] copy = new Elbow[elbows.length];
    for (int i = 0; i < elbows.length; i++) copy[i] = E(elbows[i]);
    twist_end_angles_test(copy, 1);
    
    vec vec1 = copy[copy.length - 1].circle_vectors_twisted[copy[copy.length - 1].num_of_circles][0];
    vec vec2 = copy[0].circle_vectors_twisted[0][0];
    if (norm(M(vec1, vec2))>0.001) twist_end_angles_test(elbows, -1);
    else twist_end_angles_test(elbows, 1);
  }
  
  void twist_end_angles_test(Elbow[] earray, int factor) {
    earray[0].twist_end(twist_end_angles[0]);
    for(int i=1;i<earray.length;i++) {
      
      float curr_diffK = calculate_tail_head_angle(earray[i - 1], earray[i]);
      if(curr_diffK!=curr_diffK) curr_diffK=0;
      
      earray[i].twist(curr_diffK, curr_diffK + factor * twist_end_angles[i]);
      vec vec1 = earray[i - 1].circle_vectors[earray[i - 1].num_of_circles][0];
      if (earray[i - 1].isTwisted) vec1 = V(earray[i - 1].circle_vectors_twisted[earray[i - 1].num_of_circles][0]);
      vec vec2 = earray[i].circle_vectors_twisted[0][0];
      
      if (norm(M(vec1, vec2))>0.001) earray[i].twist(-2 * curr_diffK, -2 * (curr_diffK + factor * twist_end_angles[i]));
    }
  }

  float calculate_tail_head_angle(Elbow ea, Elbow eb) {
    vec ea_vec = ea.circle_vectors[ea.num_of_circles][0];
    if (ea.isTwisted) ea_vec = ea.circle_vectors_twisted[ea.num_of_circles][0];
    vec eb_vec = eb.circle_vectors[0][0];
    if (eb.isTwisted) eb_vec = eb.circle_vectors_twisted[0][0];
    return angle(ea_vec, eb_vec);
  }
  
  float calculate_tail_head_angle(PPath p1, PPath p2) {
    vec p1_vec = p1.circle_vectors[p1.num_of_circles];
    vec p2_vec = p2.circle_vectors[0];
    return angle(p1_vec, p2_vec);
  }
}
