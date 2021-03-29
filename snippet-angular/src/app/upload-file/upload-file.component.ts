import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { NgxFileDropEntry, FileSystemFileEntry, FileSystemDirectoryEntry } from 'ngx-file-drop';
import { ImageCroppedEvent } from 'ngx-image-cropper';
import { AuthService } from '../service/auth.service';
import { UserService } from '../service/user.service';


@Component({
  selector: 'app-upload-file',
  templateUrl: './upload-file.component.html',
  styleUrls: ['./upload-file.component.css']
})
export class UploadFileComponent implements OnInit {

  private regex = /(.jpg|.png)$/;
  public files: NgxFileDropEntry[] = [];
  path : string;
  cropImage : string;
  limitSizeError : boolean = false;
  flagNotTheExactNumberOfFilesUploaded : boolean = false;
  flagNoCorrectFormat = false;
  isLoading : boolean = false;
  successLoad : boolean = false;

  constructor(
    private userService : UserService,
    private authService : AuthService
  ) {}

  ngOnInit() {
    this.authService.handleUnAuth();
  }

  public dropped(files: NgxFileDropEntry[]) {
    this.files = files;
    console.log(files);
    if ( files.length > 1) {
      this.flagNotTheExactNumberOfFilesUploaded = true;
    }
    else {
      const droppedFile = files[0];
      if (droppedFile.fileEntry.isFile) {
        const fileEntry = droppedFile.fileEntry as FileSystemFileEntry;
        fileEntry.file((file: File) => {
          console.log(droppedFile.relativePath, file);
          this.flagNoCorrectFormat = file.name.search(this.regex) == -1;
          if (!this.flagNoCorrectFormat) {
            console.log(`index : ${file.name.search(this.regex) != -1}`);
            var fr = new FileReader();
              fr.onload = () => {
                console.log(fr.result);
                this.path = fr.result as string;
              }
              fr.readAsDataURL(file);
            }
          }
        );
      } else {
        const fileEntry = droppedFile.fileEntry as FileSystemDirectoryEntry;
        console.log(droppedFile.relativePath, fileEntry);
      }
    }
  }

  imageCropped(event: ImageCroppedEvent) {
    console.log(event.base64);
    this.cropImage = event.base64;
}

  public fileOver(event){
    console.log("start");
    this.flagNotTheExactNumberOfFilesUploaded = false;
    console.log(event);
  }

  public fileLeave(event){
    console.log("end");
    console.log(event);
  }

  imageLoaded(image: HTMLImageElement) {
    console.log("image loaded");
    console.log(image);
  }

  fileChangeEvent(event: any): void {
    console.log(event);
  }

  clickSaveBtn() {
    this.isLoading = true;
    this.successLoad = false;
    this.userService.saveUserImage(this.cropImage)
      .subscribe(
        res => {
          this.isLoading = false;
          console.log(res);
          if (res["status"] == "success")
            this.successLoad = true;
        },
        err => {
          this.isLoading = false;
          if (err instanceof HttpErrorResponse) {
            if (err.error["message"] == "limit size")
              this.limitSizeError = true;
          }
        }
      )
    ;
  }
}