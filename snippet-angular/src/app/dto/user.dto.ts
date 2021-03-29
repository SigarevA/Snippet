import { AuthorityDTO } from './authority.dto';

export interface UserDTO{
    authorities: AuthorityDTO[],
    email : string,
    id : string,
    authors : string[],
    followers : string[],
    likedSnippets : string[],
    name : string,
    imagePath : string
}