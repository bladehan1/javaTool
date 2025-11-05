package demo2;

class Solution2 {

    public static void main(String[] args){
        String case1="ryyryyyrryyyyyryyyrrryyyryryyyyryyrrryryyyryrryrrrryyyrrrrryryyrrrrryyyryyryrryryyryyyyryyrryrryryy";
        Solution2 solution=new Solution2();
        int ret =solution.minimumOperations(case1);
        System.out.println("ret:"+ret);
    }

    public int minimumOperations(String leaves) {
        int len = leaves.length();
        int[][] re =new int[len][3];
        //初始化
        re[0][0]=leaves.charAt(0)=='r'?0:1;
        re[0][1]=re[0][2]=re[1][2]= Integer.MAX_VALUE;
        //约束
        //最优解 序列函数
        for(int i=1;i<len;i++){
            int switchR=leaves.charAt(i)=='r'?0:1;
            int switchY=leaves.charAt(i)=='y'?0:1;
            re[i][0]=re[i-1][0]+switchR;
            re[i][1]= Math.min(re[i-1][0],re[i-1][1])+switchY;
            if(i>=2) {
                re[i][2] = Math.min(re[i - 1][1], re[i - 1][2]) + switchR;
            }
        }
        return re[len-1][2];
    }
    public int getRsPair(String leaves,int left,int right){
        int len=leaves.length();
        if(right>=len){
            return 0;
        }
        int pairNum=0;
        while(left<right){
            while(left<right){
                if( leaves.charAt(right)=='r'){
                    break;
                }else{
                }
                right--;
            }
            while(left<right){
                if( leaves.charAt(left)=='y'){
                    leaves=swapChar(leaves,left,right,pairNum+1);
                    pairNum++;
                    break;
                }else{

                }
                left++;
            }
        }
        System.out.println(leaves);
        return pairNum;
    }
    public String swapChar(String leaves,int left,int right,int pairNum){
        System.out.println("pairNum:"+pairNum+",leaves"+"left:"+left+":"+leaves.charAt(left)+",right:"+right+":"+leaves.charAt(right));
        StringBuilder sb=new StringBuilder(leaves);
        char tmp=sb.charAt(left);
        sb.setCharAt(left,sb.charAt(right));
        sb.setCharAt(right,tmp);
        leaves=sb.toString();
        return leaves;
    }

}
