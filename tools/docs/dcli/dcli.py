"""
Generate index.html for CLI docs
"""
import os
import sys
import shutil

if len(sys.argv) != 2:
    print('Script one input directory as input')
    sys.exit(1)

VAPI_PDKROOT = os.environ.get('VAPI_PDKROOT')
if not VAPI_PDKROOT:
    print('set VAPI_PDKROOT variable')
    sys.exit(1)
index_css_path = os.path.join(VAPI_PDKROOT, 'tools', 'docs', 'dcli', 'index-style.css')
css_path = os.path.join(VAPI_PDKROOT, 'tools', 'docs', 'dcli', 'style.css')

input_dir = sys.argv[1]
output_file = os.path.join(input_dir, 'index.html')
dir_split = input_dir.rsplit('/', 1)
rel_dir = dir_split[0]
if len(dir_split) > 1:
    rel_dir += '/'

print 'Generating output index file: %s' % (output_file)
with open(output_file, 'w') as outfile:
    try:
        shutil.copy2(index_css_path, 'index_style.css')
    except IOError:
        # Ignore error in copying index css file if any
        pass

    outfile.write('<html><body>')
    outfile.write('<head><link rel="stylesheet" type="text/css" href=index-style.css">')
    for dirpath, _, filenames in os.walk(input_dir):
        if len(dir_split) > 1:
            cmd_path = dirpath.partition(rel_dir)[0:3:2][1]
        else:
            cmd_path = dirpath
        if len(filenames) > 0:
            shutil.copy2(css_path, dirpath)
        for f in filenames:
            filepath = os.path.join(dirpath, f)
            if os.stat(filepath).st_size == 0:
                continue
            # Remove the input dir from the filepath to get
            # the relative link
            rel_filepath = filepath.partition(input_dir)[2]
            if rel_filepath.startswith('/'):
                rel_filepath = rel_filepath[1:]
            outfile.write('<li><a href="%s"' % (rel_filepath))
            outfile.write('target="_parent">dcli %s %s</a></li>' % (cmd_path.replace('/', ' '), f.rsplit('.')[0].lower()))
    outfile.write('</body></html>')
