import { AuthorDTO } from './author.dto';

export interface CommentDTO {
    favorite : boolean,
    id : string,
    datePublication : Date,
    content : string,
    authorDTO : AuthorDTO
    likedUser : string[]
}