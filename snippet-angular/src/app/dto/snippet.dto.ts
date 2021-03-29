import { AuthorDTO } from './author.dto';

export interface SnippetDTO {
    id : string,
    author : AuthorDTO,
    content : string,
    datePublication : Date,
    comments : string[],
    listOfUsersLikedTheSnippet : []
    favorite : boolean;
}