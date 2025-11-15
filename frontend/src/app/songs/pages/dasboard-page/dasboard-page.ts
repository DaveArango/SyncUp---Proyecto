import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SideMenuHeader } from "../../components/side-menu-header/side-menu-header";
import { SideMenuOptions } from "../../components/side-menu-options/side-menu-options";
import { SideMenu } from "../../components/side-menu/side-menu";

@Component({
  selector: 'app-dasboard-page',
  imports: [RouterOutlet, SideMenu],
  templateUrl: './dasboard-page.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class DasboardPage { }
