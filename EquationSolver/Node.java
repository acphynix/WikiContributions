package evolution;

public abstract class Node {
	public Node(){}
	public Node (Node left, Node right){
		c=new Node[2];
		c[0]=c[0];
		c[1]=c[1];
	}
	public Node (Node left, Node right, Node parent){
		c=new Node[2];
		c[0]=c[0];
		c[1]=c[1];
		this.parent=parent;
	}
	Node[] c;
	Node parent;
	public abstract float eval();
	public void randomize(int l){
		if(l==0){
			for(int i=0;i<c.length;i++){
				c[i]=Value.random();
				c[i]=Value.random();
			}
		}
		else{
			for(int i=0;i<c.length;i++){
				c[i]=random();
				if(!(c[i] instanceof Value))c[i].randomize(l-1);
			}
		}
		//System.out.println(l+": "+toString());
	}
	public int length(){
		int l=1;
		for(int i=0;i<c.length;i++){
			if(c[i]!=null)l+=c[i].length();
		}
		return l;
	}
	protected abstract void cleanself();
	public void clean(){
		// clean all children; then clean self.
		for(int i=0;i<c.length;i++)if(c[i]!=null)c[i].clean();
		cleanself();
	}
	public static Operator operator(char operator){
		return new Operator(operator, new Node[2]);
	}
	public static Value valuei(int val){
		return new Value(val);
	}
	public static Value valuef(float val){
		Value.vals.add(val);
		if(Value.vals.contains(val))return new Value(Value.vals.indexOf(val));
		else return new Value(Value.vals.size()-1);
	}
	public static Constant consti(int val){
		return new Constant(val);
	}
	public static Constant constf(float val){
		Constant.consts.add(val);
		if(Constant.consts.contains(val))return new Constant(Constant.consts.indexOf(val));
		else return new Constant(Constant.consts.size()-1);
	}
	public static Node random(){
		if(Math.random()>0.5){
			return Operator.random();
		}else{
			return Value.random();
		}
	}
}
