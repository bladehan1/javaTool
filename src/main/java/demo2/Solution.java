package demo2;

class Solution {

    public static void main(String[] args){
        String case1="ryyryyyrryyyyyryyyrrryyyryryyyyryyrrryryyyryrryrrrryyyrrrrryryyrrrrryyyryyryrryryyryyyyryyrryrryryy";
        Solution solution=new Solution();
        int ret =solution.minimumOperations(case1);
        System.out.println("ret:"+ret);
    }

    public int minimumOperations(String leaves) {
        int rLen=0;
        int yLen=0;
        int len=leaves.length();
        System.out.println("len:"+len);
        for(int i=0;i<len;i++){
            if(leaves.charAt(i)=='r'){
                rLen++;
            }else{
                yLen++;
            }
        }
        if(rLen<2){
            return 3;
        }
        int right=len-1;
        int left=0;
        int third=1;
        if(leaves.charAt(right)=='r'){
            third=0;
        }
        for(;right>=0;right--){
            if(leaves.charAt(right)=='y'){
                right=right-1;
                break;
            }
        }

        return this.getRsPair(leaves,left,right)+third;
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
