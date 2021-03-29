import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { SnippetDTO } from '../dto/snippet.dto';
import { map } from 'rxjs/internal/operators';

@Injectable({
  providedIn: 'root'
})
export class SnippetService {

  constructor(private http : HttpClient) { }

  public getAllSnippet() : Observable<SnippetDTO[]> {
    const url = `${environment.urlAPI}/api/snippet/all`;
    return this.http.get(url).pipe(
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

  public liskeSnippet(snippetID : string) : Observable<SnippetDTO> {
    const url = `${environment.urlAPI}/api/snippet/like`;
    return this.http.post(url, {id : snippetID}).pipe(
      map (
        value => {
          return this.converterObjectToSnippetDTO(value.valueOf())
        }
      )
    );
  }

  public createSnippet(content : string) {
    const url = `${environment.urlAPI}/api/snippet/create`;

    return this.http.post(url, {content : content});
  }

  private converterObjectToSnippetDTO(value : any) : SnippetDTO {
    console.log(value);
    let date = <number>value["datePublication"];
    console.log(date);
    let da = new Date(date);
    console.log(da);
    console.log(da.toLocaleString());
    let _snippet = <SnippetDTO>value  
    _snippet.datePublication = da;
    console.log("te");
    return _snippet;
  }

  public getUserSnippetsByID(id : string) : Observable<SnippetDTO[]> {
    const url = `${environment.urlAPI}/api/snippet/all/${id}`;

    return this.http.get(url).pipe(
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
  );;
  }

  public getTimeStamp() : Observable<any> {
    const url = `${environment.urlAPI}/api/snippet/timestamp`;
    return this.http.get(url);
  }
}