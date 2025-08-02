export interface RegisterUserDTO extends AuthenticationFormDTO {
  firstName: string;
  lastName: string;
}

export interface AuthenticationFormDTO {
  email: string;
  password: string;
}

export interface AuthenticationSuccesDTO {
  access_token: string;
  refresh_token: string;
  user: UserDTO;
}

export interface UserDTO {
  firstName: string;
  lastName: string;
  email: string;
  profileImageId: string;
  accountLocked: boolean;
  enabled: boolean;
  roles: Array<String>;
  authorities: Array<String>;
  publicId: string;
}

export interface AccountValidationDTO {
  code: string;
  verificationToken: string;
}
