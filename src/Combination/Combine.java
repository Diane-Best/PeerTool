package Combination;

import java.io.File;
import java.util.ArrayList;

import org.dom4j.DocumentException;

import PetriNet.PetriNet;

public class Combine {
	ArrayList<String> transitions = new ArrayList<String>();
	ArrayList<String> places = new ArrayList<String>();
	ArrayList<String> Asyncplaces = new ArrayList<String>();
	ArrayList<String> Asyncplaces2 = new ArrayList<String>();
	ArrayList<Integer> transNum = new ArrayList<Integer>();
	int XMLNum;
	ArrayList<String> fileNames = new ArrayList<String>();
	PetriNet petri = new PetriNet();
	int Asyncmatrix [][];
	int Asyncmatrix2 [][];
	ArrayList<String> transMerge = new ArrayList<String>();
	int [][] SyncInitial;
	int [][] SyncCombined;
	String tempFile = "C:/PetriNetSyn/MergeTemp.xml";

	PetriNet SyncResultPetri;
	PetriNet AsyncResultPetri;
	PetriNet AsyncResultPetri2;
	PetriNet SimpleResultPetri;
	
	public Combine(ArrayList<String> fileNames) throws DocumentException{
		File file = new File("C:\\PetriNetSyn");
		if  (!file .exists()  && !file .isDirectory())      
		{       
		    System.out.println("//不存在");  
		    file.mkdir();  
		    
		} else   
		{  
		    System.out.println("//目录存在");  
		}
		XMLNum = fileNames.size();
		petri.mergeXML(tempFile, fileNames);
		transitions.addAll(petri.getTransitions());
		transNum.addAll(petri.getTransNum());
		places.addAll(petri.getPlaces());
		SyncInitial = petri.generateIncidenceMatrix();
		SyncCombined = new int[places.size()][transitions.size()];
		Asyncmatrix = new int[XMLNum][transitions.size()];
		Asyncmatrix2 = new int[transitions.size()/2][transitions.size()];
	}
	
	private void AsyncMatrix(){
		int flag1 = 0;
		int flag2 = 0;
		int row = 0;
		for(int i=0,j=0;i<transitions.size();i++){
			flag1 = i;
			if(flag1>=transNum.get(j)){
				j++;
				flag2 = transNum.get(j);
			}
			flag2 = transNum.get(j);
		for(;flag2<transitions.size();flag2++){
			String trans1 = transitions.get(flag1).replaceAll("[0-9]{1,}", "");// 去掉变迁名中的数字;
			String trans2 = transitions.get(flag2).replaceAll("[0-9]{1,}", "");
			if(trans1
				  .regionMatches(1, trans2, 1, trans1.length()-1)
				  &&((trans1.startsWith("?")&&trans2.startsWith("!"))||
					(trans1.startsWith("!")&&trans2.startsWith("?")))){
					if(trans1.startsWith("?")){
						Asyncmatrix[j][flag1] = -1;
						Asyncmatrix[j][flag2] = 1;
					}
					if(trans2.startsWith("?")){
						for(int n=0;n<XMLNum;n++){
							if(flag2<transNum.get(n)){
								row = n;
								break;
							}
						}
						Asyncmatrix[row][flag2] = -1;
						Asyncmatrix[row][flag1] = 1;
					}
				}
			else 
				continue;
		}
	}
	}
	
	private int AsyncMatrix2(){
		int row = -1;
		ArrayList<String> existTrans = new ArrayList<>();
		boolean exist = false;
		int existRow = 0;
		int tempRow = 0;
		for(int i=0;i<transitions.size()-1;i++){
			exist = false;
			System.out.println(transitions.get(i)+"----------------");
		for(int j=i+1;j<transitions.size();j++){
			String trans1 = transitions.get(i).replaceAll("[0-9]{1,}", "");// 去掉变迁名中的数字;
			String trans2 = transitions.get(j).replaceAll("[0-9]{1,}", "");
			if(trans1
				  .regionMatches(1, trans2, 1, trans1.length()-1)
				  &&((trans1.startsWith("?")&&trans2.startsWith("!"))||
					(trans1.startsWith("!")&&trans2.startsWith("?")))
					&&(transitions.get(i).charAt(0)!=transitions.get(j).charAt(0))
					){
				    for(int n=0;n<existTrans.size();n++){//判断是否已存在与当前变迁相同的变迁，存在则关联矩阵在同一行
				    	if(existTrans.get(n).equals(trans1)){
				    			exist = true;
				    			tempRow = row;
				    			row = n;
				    			existRow = n;
				    	}
				    }
				    if(!exist){
				    	if(row == existRow)
				    		row = tempRow;
				    	row++;
					    existTrans.add(trans1);				    	
				    }
					if(trans1.startsWith("?")){
						Asyncmatrix2[row][i] = -1;
						Asyncmatrix2[row][j] = 1;
					}
					if(trans2.startsWith("?")){
						Asyncmatrix2[row][j] = -1;
						Asyncmatrix2[row][i] = 1;
					}
				}
			else 
				continue;
		}
	}
		return row;
	}
	
	public int analyzeAsyncMatrix(String AsyncresultFile){
		AsyncMatrix();
		AsyncResultPetri = new PetriNet(AsyncresultFile);
		for(int i =0;i<XMLNum;i++){
			Asyncplaces.add("P"+i);
		}
		AsyncResultPetri.copyXML(tempFile);
		for(int i = 0; i < XMLNum; i++){
			boolean hasPlace = false;
			for(int j = 0; j < transitions.size(); j++){
				if(Asyncmatrix[i][j]==1){
					if(hasPlace == false){
					AsyncResultPetri.addPlace(Asyncplaces.get(i), "0", "0", "0.0", "0.0", "0.0", "0.0", "0");
					hasPlace = true;
					}
					addArc(AsyncResultPetri,transitions.get(j),Asyncplaces.get(i),
							AsyncResultPetri.getPlaceLocation(Asyncplaces.get(i)),
							AsyncResultPetri.getTransitionLocation(transitions.get(j)));
				}
				else if(Asyncmatrix[i][j]==-1){
					if(hasPlace == false){
						AsyncResultPetri.addPlace(Asyncplaces.get(i), "0", "0", "0.0", "0.0", "0.0", "0.0", "0");				
					hasPlace = true;
					}
					addArc(AsyncResultPetri,Asyncplaces.get(i),transitions.get(j),
							AsyncResultPetri.getPlaceLocation(Asyncplaces.get(i)),
							AsyncResultPetri.getTransitionLocation(transitions.get(j)));

				}
				else if(Asyncmatrix[i][j]==0)
					continue;
			}
		}
		return 1;
	}
	
	public int analyzeAsyncMatrix2(String AsyncresultFile){
		int rowNum = AsyncMatrix2();
		AsyncResultPetri2 = new PetriNet(AsyncresultFile);
		for(int i=0;i<=rowNum;i++){
			Asyncplaces2.add("P"+i);
		}
		AsyncResultPetri2.copyXML(tempFile);
		for(int i = 0; i <= rowNum; i++){
			boolean hasPlace = false;
			for(int j = 0; j < transitions.size(); j++){
				if(Asyncmatrix2[i][j]==1){
					if(hasPlace == false){
					AsyncResultPetri2.addPlace(Asyncplaces2.get(i), "0", "0", "0.0", "0.0", "0.0", "0.0", "0");
					
					hasPlace = true;
					}
					addArc(AsyncResultPetri2,transitions.get(j),Asyncplaces2.get(i),
							AsyncResultPetri2.getPlaceLocation(Asyncplaces2.get(i)),
							AsyncResultPetri2.getTransitionLocation(transitions.get(j)));
				}
				else if(Asyncmatrix2[i][j]==-1){
					if(hasPlace == false){
						AsyncResultPetri2.addPlace(Asyncplaces2.get(i), "0", "0", "0.0", "0.0", "0.0", "0.0", "0");				

						hasPlace = true;
					}
					addArc(AsyncResultPetri2,Asyncplaces2.get(i),transitions.get(j),
							AsyncResultPetri2.getPlaceLocation(Asyncplaces2.get(i)),
							AsyncResultPetri2.getTransitionLocation(transitions.get(j)));

				}
				else if(Asyncmatrix2[i][j]==0)
					continue;
			}
		}
		return 1;
	}
	
	private void SyncMartix(){
		int row = 0;
		String mergeTrans1 = "";
		String mergeTrans2 = "";
		int comLength = 0;
		boolean ifmatch = false;
		ArrayList<String> transForJudge = new ArrayList<String>();//用于判断该变迁是否存在对应变迁
		for(int i = 0;i<transitions.size();i++){
			for(int j= i+1;j<transitions.size();j++){
				String trans1 = transitions.get(i).substring(1);
				String trans2 = transitions.get(j).substring(1);
				//判断是否为多对多
				if (transitions.get(i).replaceAll("[0-9]{1,}", "").length() < (trans1.length())
					&& transitions.get(j).replaceAll("[0-9]{1,}", "").length() < (trans2.length())) {
					trans1 = transitions.get(i).substring(1);
					trans2 = transitions.get(j).substring(1);
				} else {
					trans1 = transitions.get(i).replaceAll("[0-9]{1,}", "");// 去掉变迁名中的数字;
					trans2 = transitions.get(j).replaceAll("[0-9]{1,}", "");
				}
				if(trans1.length()>=trans2.length())
					comLength = trans1.length() - 1;
				else {
					comLength = trans2.length() - 1;
				}
				if(trans1.regionMatches(1, trans2, 1, comLength)
				  &&((trans1.startsWith("?")&&trans2.startsWith("!"))||
					(trans1.startsWith("!")&&trans2.startsWith("?")))){
					mergeTrans1 = transitions.get(i).replaceAll("[^\\w\\s]+", "");
					mergeTrans2 = transitions.get(j).replaceAll("[^\\w\\s]+", "");
					ifmatch = true;
					if(mergeTrans1.length()>= mergeTrans2.length()){
					    transMerge.add(mergeTrans1);
					    transForJudge.add(transitions.get(i).replaceAll("[0-9]{1,}", ""));

					}
					else{
						transMerge.add(mergeTrans2);
						transForJudge.add(transitions.get(i).replaceAll("[0-9]{1,}", ""));			
					}
					for(int n=0;n<places.size();n++){
						SyncCombined[n][row] = SyncInitial[n][i]+SyncInitial[n][j];
					}
					row++;
				}
		}
		if(!ifmatch){
			boolean exist = false;
			for(int n=0;n<transMerge.size();n++){
				if(transMerge.get(n).contains(transitions.get(i).replaceAll("[0-9]{1,}[^\\w\\s]+", ""))
					&&(!transForJudge.get(n).equals(transitions.get(i).replaceAll("[0-9]{1,}", "")))){
					exist = true;
				}
			}
			 if(!exist){
			 transMerge.add(transitions.get(i).replaceAll("[^\\w\\s]+", ""));
			 transForJudge.add(transitions.get(i).replaceAll("[0-9]{1,}", ""));
				System.out.println("get(i)-"+transitions.get(i));
			 for(int n=0;n<places.size();n++){
				 SyncCombined[n][row] = SyncInitial[n][i];
			 }
			 row++;
			 }
		}
		ifmatch=false;
		}
	}
	
	
	public int analyzeSyncMatrix(String SyncresultFile){
		SyncMartix();
		output();
		SyncResultPetri = new PetriNet(SyncresultFile);
		SyncResultPetri.copyXML(tempFile);
		System.out.println(places.size());
		for(int n=0;n<transitions.size();n++){
			boolean exist = false;
			for(int m=0;m<transMerge.size();m++){
				String transName = transitions.get(n).replaceAll("[^\\w\\s]+", "");
				if(transName.equals(transMerge.get(m))){
					exist = true;
					SyncResultPetri.modifyTransition(transitions.get(n), transMerge.get(m));
				}
			}
			if(!exist){
				SyncResultPetri.deleteTransition(transitions.get(n));
			}
		}
		for(int i=0;i<places.size();i++){
			for(int j=0;j<transMerge.size();j++){
				if(SyncCombined[i][j]==2){
					addArc(SyncResultPetri,transMerge.get(j),places.get(i),
							SyncResultPetri.getTransitionLocation(transMerge.get(j)),
							SyncResultPetri.getPlaceLocation(places.get(i)));
	
					addArc(SyncResultPetri,places.get(i),transMerge.get(j),
							SyncResultPetri.getPlaceLocation(places.get(i)),
							SyncResultPetri.getTransitionLocation(transMerge.get(j)));
				}
			    else if(SyncCombined[i][j]==1){
					addArc(SyncResultPetri,transMerge.get(j),places.get(i),
							SyncResultPetri.getTransitionLocation(transMerge.get(j)),
							SyncResultPetri.getPlaceLocation(places.get(i)));

				}
				else if(SyncCombined[i][j]==-1){	
					addArc(SyncResultPetri,places.get(i),transMerge.get(j),
							SyncResultPetri.getPlaceLocation(places.get(i)),
							SyncResultPetri.getTransitionLocation(transMerge.get(j)));
				}
			}
		}
		return 1;
	}
	
	public int simplify(String simpleResultFile){
		SimpleResultPetri = new PetriNet(simpleResultFile);
		SimpleResultPetri.copyXML(SyncResultPetri.getFileName());
		SimpleResultPetri.simplyfy();
		return 1;
	}
	
	private void addArc(PetriNet petri,String source,String target,
			            String[] point1,String[] point2){
		petri.addArc(source, target, point1, point2);
	}
	
	public void output(){
		for(int i=0;i<places.size();i++){
			for(int j=0;j<transMerge.size();j++){
				System.out.print(SyncCombined[i][j]+" ");
				System.out.print(transMerge.get(j)+" ");
			}

			System.out.print(places.get(i)+" ");
			System.out.println("\r");
		}
	}
	
}
