import { Component } from '@angular/core';
import { SideMenuHeader } from "../side-menu-header/side-menu-header";
import { SideMenuOptions } from "../side-menu-options/side-menu-options";

@Component({
  selector: 'songs-side-menu',
  imports: [SideMenuHeader, SideMenuOptions],
  templateUrl: './side-menu.html',
})
export class SideMenu { }
