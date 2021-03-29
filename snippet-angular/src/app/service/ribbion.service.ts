import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { SnippetDTO } from '../dto/snippet.dto';
import { map } from 'rxjs/internal/operators';
import { TapeTypeDto } from '../dto/tape.type.dto';
import { TapeTypeHub } from '../dto/tape.types.hub';

@Injectable({
  providedIn: 'root'
})
export class RibbionService {

  constructor(private http : HttpClient) { }

  public getTapeTypeList() : Observable<TapeTypeHub> {
    let url = `${environment.urlAPI}/api/types`;
    return this.ArrayTapeTypeToTapeTypeHub(this.http.get<TapeTypeDto[]>(url));
  }

  private ArrayTapeTypeToTapeTypeHub(event : Observable<TapeTypeDto[]>) : Observable<TapeTypeHub> {
    return event.pipe(
      map( (v, i) => {
          return new TapeTypeHub(v);
        }
      )
    );
  }

  public setupTapeType(name : string) : Observable<TapeTypeHub> {
    const url = `${environment.urlAPI}/api/tape/type`;
    return this.ArrayTapeTypeToTapeTypeHub(
      this.http.post<TapeTypeDto[]>(
        url,
        {name : name}
      )
    );
  }

  public getTapeSnippets(timestamp : number, pageNum : number) : Observable<SnippetDTO[]> {
    let url = `${environment.urlAPI}/api/snippet/tape/${timestamp}`;
    if (pageNum != null) 
      url += `?pageNum=${pageNum}`;
    return this.http.get<SnippetDTO[]>(url).pipe(
      map(
        x => {
          let fd = <any[]>x.valueOf();
          console.log(fd);
          let snippets : SnippetDTO[] = [];
          let i = 0;
          for (let snippet of fd) {
            let _snippet = this.converterObjectToSnippetDTO(snippet);
            console.log(_snippet);
            snippets[i] = _snippet;
            i++;
          }
          return snippets;
        }
      )
    );
  }

  private converterObjectToSnippetDTO(value : any) : SnippetDTO {
    console.log(value);
    let date = <number>value["datePublication"];
    console.log(date);
    let da = new Date(date);
    console.log(da);
    console.log(da.toDateString());
    let _snippet = <SnippetDTO>value  
    _snippet.datePublication = da;
    console.log("te");
    return _snippet;
  }
}