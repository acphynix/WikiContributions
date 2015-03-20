package evolution;

public class Evolver {
	public static Node random(){
		if(Math.random()>0.5){
			return Node.operator(Operator.set[(int)(Math.random()*(Operator.set.length))]);
		}else{
			return Node.valuei((int)(Math.random()*(Value.vals.size())));
		}
	}
	public float round(float in){
		return (float) (Math.round(in * 10000) / 10000.0);
	}
	public float round(double in){
		return (float) (Math.round(in * 10000) / 10000.0);
	}
	public float func(float... in){
		//return (float) Math.tan(Math.toRadians(Math.sin(Math.toRadians(in[0]*in[0]*in[0]))));
		double d=in[0]*7;
		return (float)d;
	}
	public boolean works(Node n, float[][] vals){
		for(int i=0;i<vals.length;i++){
			Value.vals.clear();
			for(int j=0;j<vals[i].length-1;j++){
				Value.vals.add(vals[i][j]);
			}
			float o=n.eval();
			if(round(o)!=round(vals[i][vals[i].length-1]))return false;
		}
		return true;
	}
	public void testEvol(){
		//Test evolution from y=x to y=7x;
		float[][] input=new float[10][2];
		for(int i=0;i<input.length;i++){
			input[i][1]=round(Math.random()*100);
			input[i][0]=round(func(input[i][1]));
			System.out.println("<"+input[i][1]+"->"+input[i][0]);
		}
		Value v=Node.valuei(0);
	}
	public Node solve(float[][] input, int minitr, int maxitr){
		Node n=null;
		//System.exit(0);
		Node temp=null;
		for(int i=0;i<maxitr;i++){
			n=Operator.random();
			n.randomize(5);
			if(works(n, input)){
				System.out.println("("+i+")A: "+n);
				//n.clean();
				//System.out.println("("+i+") -["+n.length()+"]: "+n.toString());
				//break;
				if(temp==null||(n.length()<temp.length())){
					temp=n;
				}
				if(i>minitr)return temp;
			}
		}
		return temp;
	}
	public void run(){
		float[][] input=new float[10][2];
		for(int i=0;i<input.length;i++){
			input[i][1]=round(Math.random()*100);
			input[i][0]=round(func(input[i][1]));
			//System.out.println("<"+input[i][1]+"->"+input[i][0]);
		}
		input=new float[][]{{0,0},{2,4}, {3,9}};
		Node n=solve(input,1000,1000000000);
		System.out.println(n+", "+n.length());
		System.out.println(n.eval());
		Node r = Node.operator('+');
		//System.out.println("________");
		//System.out.println(n.toString());
	}
	public static void main(String[] args) {
		Evolver e=new Evolver();
		e.run();
		//e.testEvol();
	}

}
