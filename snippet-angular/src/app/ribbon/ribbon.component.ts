import { Component, OnInit } from '@angular/core';
import { AuthService } from '../service/auth.service';
import { Router, ActivatedRoute } from '@angular/router';
import { SnippetService } from '../service/snippet.service';
import { SnippetDTO } from '../dto/snippet.dto';
import { RibbionService } from '../service/ribbion.service';
import { TapeTypeDto } from '../dto/tape.type.dto';
import { TapeTypeHub } from '../dto/tape.types.hub';
import { flatMap, tap } from 'rxjs/internal/operators';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-ribbon',
  templateUrl: './ribbon.component.html',
  styleUrls: ['./ribbon.component.css']
})
export class RibbonComponent implements OnInit {

  favorite : boolean = true;
  snippets : SnippetDTO[] = undefined;
  tapeTypes : TapeTypeHub;
  errorShow : boolean = false;
  private timestamp : number; 
  private pageNum : number;
  private pageSize : number = 15;
  fullListIsDisplayed : boolean = false;

  isLoading : boolean = false;

  constructor(
    private authService : AuthService,
    private route: ActivatedRoute,
    private snippetService : SnippetService,
    private ribbionService : RibbionService
    ) { }

  ngOnInit() {
    this.authService.handleUnAuth();
    this.loadTapeType();
    this.loadSnippetsNew();
    this.pageNum = 0;
  }

  loadSnippetsNew() {
    this.snippetService.getTimeStamp()
    .pipe(
      tap(x => {
        this.timestamp = x;
      })
    )
    .pipe(
      flatMap(
        (x, i) => {
          return this.ribbionService.getTapeSnippets(x, this.pageNum);
        }
      )
    )      
    .subscribe(
      res => {
        this.handleLoadSnippet(res);
      },
      err => {
        this.handleError(err);
      }
    )
  }

  loadTapeType() {
    this.ribbionService.getTapeTypeList()
      .subscribe(
        res => {
          this.tapeTypes = res;
          console.log(res);
        },
        err => {
          this.handleError(err);
        }
      )
  }

  loadSnippet() {
    this.isLoading = true;
    this.ribbionService.getTapeSnippets(this.timestamp, this.pageNum)
      .subscribe(
        res => {
          this.handleLoadSnippet(res);
        },
        err => {
          this.handleError(err);
        }
      )
  }  

  pickTapeType(tapeType : string) {
    this.pageNum = 0;
    this.ribbionService.setupTapeType(tapeType).subscribe(
      res => {
        console.log(res);
        this.tapeTypes = res;
        this.loadSnippet();
      },
      err => {
        this.handleError(err);
      }
    )
  }

  handleError(err : HttpErrorResponse) {
    console.log(err);
    switch (err.status) {
      case 0:
        this.errorShow = true;
        break;
    }
  }

  clickLoadSnippets() {
    console.log(this.pageNum);
    this.pageNum++;
    console.log(this.pageNum);
    this.loadSnippet()
  }

  private handleLoadSnippet(snippets : SnippetDTO[]) {
    if (this.pageNum != 0) 
      this.snippets = this.snippets.concat(snippets)
    else 
      this.snippets = snippets;
    this.fullListIsDisplayed = snippets.length != this.pageSize;
  }
}