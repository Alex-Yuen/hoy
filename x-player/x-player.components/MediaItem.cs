using System;
using System.Collections.Generic;
using System.Text;

namespace xplayer
{
    class MediaItem
    {
        private string _name;
        private string _path;
        private string _type;
        private string _duration;

        public string Name
        {
            get { return this._name; }
            set { this._name = value; }
        }

        public string Path
        {
            get { return _path; }
            set { this._path = value; }
        }

        public string Type
        {
            get { return _type; }
            set { this._type = value; }
        }

        public string Duration
        {
            get { return _duration; }
            set { this._duration = value; }
        }
    }
}
