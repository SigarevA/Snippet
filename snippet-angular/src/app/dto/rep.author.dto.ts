import { AuthorDTO } from './author.dto';

export interface RepresentationAuthorDTO extends AuthorDTO {
    subscription : boolean
}