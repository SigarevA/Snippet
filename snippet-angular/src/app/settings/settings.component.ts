import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { TapeTypeHub } from '../dto/tape.types.hub';
import { AuthService } from '../service/auth.service';
import { RibbionService } from '../service/ribbion.service';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css']
})
export class SettingsComponent implements OnInit {

  tapeTypes : TapeTypeHub;
  errorShow : boolean = false;


  constructor(
    private authService : AuthService,
    private ribbionService : RibbionService
  ) { }

  ngOnInit() {
    this.authService.handleUnAuth();
    this.loadTapeType();
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

  handleError(err : HttpErrorResponse) {
    console.log(err);
    switch (err.status) {
      case 0:
        this.errorShow = true;
        break;
    }
  }

  pickTapeType(tapeType : string) {
    this.ribbionService.setupTapeType(tapeType).subscribe(
      res => {
        console.log(res);
        this.tapeTypes = res;
      },
      err => {
        this.handleError(err);
      }
    )
  }
}