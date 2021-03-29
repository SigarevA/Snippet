import { TapeTypeDto } from "./tape.type.dto";

export class TapeTypeHub {

    tapeTypes : TapeTypeDto[] 

    constructor(tapeTypes : TapeTypeDto[]) {
        this.tapeTypes = tapeTypes;
    }

    toRepresentation(name : string) : string {
        switch(name) {
            case "all": {
                return "Всё";
            }
            case "subscription" : {
                return "Подписка";
            }
            default :{
                return undefined;
            }
        }
    }

}