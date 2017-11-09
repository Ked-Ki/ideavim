/*
 * IdeaVim - Vim emulator for IDEs based on the IntelliJ platform
 * Copyright (C) 2003-2016 The IdeaVim authors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.maddyhome.idea.vim.ex.handler;

import com.intellij.codeInsight.actions.FormatChangedTextUtil;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.ChangedRangesInfo;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.util.diff.FilesTooBigForDiffException;
import com.maddyhome.idea.vim.ex.CommandHandler;
import com.maddyhome.idea.vim.ex.CommandName;
import com.maddyhome.idea.vim.ex.ExCommand;
import com.maddyhome.idea.vim.helper.PsiHelper;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class WriteHandler extends CommandHandler {
  public WriteHandler() {
    super(new CommandName[]{
      new CommandName("w", "rite")
    }, RANGE_OPTIONAL | ARGUMENT_OPTIONAL);
  }

  /***
   * Edited to perform code reformat. This is kludgy.
   *
   * TODO: implement :command ex option so you can set this in .ideavimrc
   *
   * @param editor  The editor to perform the action in.
   * @param context The data context
   * @param cmd     The complete Ex command including range, command, and arguments
   * @return True if able to reformat, false otherwise.
   */
  public boolean execute(@NotNull Editor editor, @NotNull DataContext context, @NotNull ExCommand cmd) {
    try {
      PsiFile file = PsiHelper.getFile(editor);
      Project project = editor.getProject();
      if (file != null && project != null) {
        ChangedRangesInfo info = FormatChangedTextUtil.getInstance().getChangedRangesInfo(file);
        if (info != null) {
          CodeStyleManager.getInstance(project).reformatTextWithContext(file, info);
          return true;
        }
      }
    }
    catch (FilesTooBigForDiffException e) {
      if (logger.isDebugEnabled()) {
        logger.debug("couldn't reformat file: ", e.getMessage());
      }
    }
    return false;
  }

  private static final Logger logger = Logger.getInstance(WriteHandler.class.getName());
}
