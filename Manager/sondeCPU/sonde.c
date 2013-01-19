#include <stdio.h>
#include <strings.h>
#include <unistd.h>
#include <ctype.h>
void main(int argc, char** args){
	FILE *fCPU, *fMemory, *fStats;
	char* file_name=args[1];
	char line[255];
	char aux[255],champ[255],*aux_aux,*sptr;
	int delai=5;
	long time=0;
	if(argc==2 && strcmp("-h",args[1])==0){
		printf("Voici la syntaxe: ./sonde <fichier de sortie> delai(s)\n");
		return;
	}
	int status = remove(file_name);
        if( status == 0 )
        	printf("%s file deleted successfully.\n",file_name);
	else{
		printf("Unable to delete the file\n");
	}
	if(argc>2)
		delai=atoi(args[2]);
	//Ouverture des fichiers
	fStats=fopen(file_name,"w");
	if( fStats == NULL){
		perror("Error ");
		return;
	}
	long free=0,memTotal=0;
	int pourcentage,nbValue,i;
	fprintf(fStats,"Time(s) Mem CPU IOwait\n");
	while(1){
		fMemory=fopen("/proc/meminfo","r");	
		memset(line,'\0',255);
		free=0;memTotal=0;
		nbValue=0;
		while( (fgets(line,254,fMemory)) != NULL ){
			memset(champ,'\0',255);
			memset(aux,'\0',255);
			strncpy(champ,line,strlen(line)-strlen(index(line,':')));
			strncpy(aux,index(line,':')+1,strlen(index(line,':'))-4);
			if(strcmp(champ,"MemTotal")==0){				
				memTotal=atol(aux);				
				nbValue++;
			}else
			if(strcmp(champ,"MemFree")==0 || strcmp(champ,"Buffers")==0 || strcmp(champ,"Cached")==0){				
				free+=atol(aux);
				nbValue++;
			}
			if(nbValue==4){
				pourcentage=100*(memTotal-free)/memTotal;
				fprintf(fStats,"%ld %d",time,pourcentage);
				nbValue=0;
			}
			memset(line,'\0',255);
		}
		memset(line,'\0',255);
		long double a[7],b[7],result[7],loadavg;
		fCPU=fopen("/proc/stat","r");
		fscanf(fCPU,"%*s %Lf %Lf %Lf %Lf %Lf %Lf %Lf",&a[0],&a[1],&a[2],&a[3],&a[4],&a[5],&a[6]);
		a[3]+=a[0]+a[1]+a[2];
		fclose(fCPU);
		sleep(1);
		fCPU = fopen("/proc/stat","r");
		fscanf(fCPU,"%*s %Lf %Lf %Lf %Lf %Lf %Lf %Lf",&b[0],&b[1],&b[2],&b[3],&b[4],&b[5],&b[6]);
		b[3]+=b[0]+b[1]+b[2];
		fclose(fCPU);
		long double delta = b[3] - a[3];
		if (delta != 0){
			result[1] = 100 * (b[0] - a[0]);
			result[2] = 100 * (b[1] - a[1]);
			result[3] = 100 * (b[2] - a[2]);
			result[6] = 100 * (b[5] - a[5]);
			result[0] = (result[1] + result[2]+ result[3]) / delta;
			result[1] /= delta;
			result[2] /= delta;
			result[3] /= delta;
			result[4] /= delta;
			result[6] /= delta;
			for (i=0 ; i<7 ; i++){
				if (result[i] > 100){
					result[i] = 100;
				}
			}
		}else{
			result[0]=0;
		}
		fprintf(fStats," %LF %LF\n",result[0],result[6]);
		fflush(fStats);
		fseek(fMemory, 0, SEEK_SET);
		sleep(delai);
		time+=delai+1;	
		fclose(fMemory);
	}
	fclose(fStats);
}

